package com.amstlan.yygh.user.service.impl;

import com.amstlan.yygh.common.exception.YyghException;
import com.amstlan.yygh.common.helper.JwtHelper;
import com.amstlan.yygh.common.result.ResultCodeEnum;
import com.amstlan.yygh.enums.AuthStatusEnum;
import com.amstlan.yygh.model.acl.User;
import com.amstlan.yygh.model.user.Patient;
import com.amstlan.yygh.model.user.UserInfo;
import com.amstlan.yygh.model.user.UserLoginRecord;
import com.amstlan.yygh.user.mapper.UserInfoMapper;
import com.amstlan.yygh.user.service.PatientService;
import com.amstlan.yygh.user.service.UserInfoService;
import com.amstlan.yygh.vo.user.LoginVo;
import com.amstlan.yygh.vo.user.UserAuthVo;
import com.amstlan.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends
        ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private  UserInfoMapper userInfoMapper;

    @Autowired
    private PatientService patientService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {

        //获取手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //判断手机号和验证码是否空
        if (phone == null || code == null){
            log.debug("手机号或者验证码为空");
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //TODO 判断验证码是否正确，需要阿里云服务
        String redisCode = (String) redisTemplate.opsForValue().get(phone);
        if (!code.equals(redisCode)){
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

//绑定手机号码
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.selectWxInfoOpenId(loginVo.getOpenid());
            if(null != userInfo) {
                //TODO 这里有大bug
                //如果这里执行了需要把某些数据复制给当前类再保存，具体要不要我后面再权衡
                QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
                wrapper.eq("phone",phone);
                UserInfo userInfoExist = baseMapper.selectOne(wrapper);
                if (userInfoExist != null){
                    userInfo.setName(userInfoExist.getName());
                    userInfo.setStatus(userInfoExist.getStatus());
                    userInfo.setAuthStatus(userInfoExist.getAuthStatus());
                    userInfo.setCertificatesNo(userInfoExist.getCertificatesNo());
                    userInfo.setCertificatesType(userInfoExist.getCertificatesType());
                    userInfo.setCertificatesUrl(userInfoExist.getCertificatesUrl());

                    Long userInfoId = userInfoExist.getId();
                    List<Patient> allUserPatientList = patientService.findAllUserId(userInfoId);

                    if(allUserPatientList.size() > 0){
                        Long realId = userInfo.getId();
                        allUserPatientList.stream().forEach(item -> {
                            item.setUserId(realId);
                            patientService.save(item);
                        });
                    }


                    baseMapper.deleteById(userInfoExist.getId());
                    baseMapper.updateById(userInfo);
                }

                userInfo.setPhone(loginVo.getPhone());

                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        if (userInfo == null){
            //判断是否是第一次登录
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone",phone);
            userInfo = baseMapper.selectOne(wrapper);

            //是第一次
            if(userInfo == null){
                log.debug("额米有数据");
                //添加数据到数据库
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                //1可用，0不可用
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }

        if (userInfo.getStatus() == 0){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //不是第一次

//记录登录

        //返回登录信息
        //返回登录用户名
        //返回token信息
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //生成并返回token 需要JWT
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;

    }

    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper();
        wrapper.eq("openid", openid);
        UserInfo userInfo = baseMapper.selectOne(wrapper);

        return userInfo;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);

        //设置认证信息
        userInfo.setName(userAuthVo.getName());
        //其他认证信息
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());

        //信息更新
        baseMapper.updateById(userInfo);
    }

    //用户列表条件查询带分页
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> infoPage, UserInfoQueryVo userInfoQueryVo) {
        //获取条件值
        //用户名称
        String keyword = userInfoQueryVo.getKeyword();
        //用户状态
        Integer status = userInfoQueryVo.getStatus();
        //认证状态
        Integer authStatus = userInfoQueryVo.getAuthStatus();
        //开始时间
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        //结束时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();
        //对条件值进行判断
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(keyword)){
            queryWrapper.like("name",keyword);
        }
        if(!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)){
            queryWrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            queryWrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.le("create_time",createTimeEnd);
        }

        Page<UserInfo> userInfoPage = baseMapper.selectPage(infoPage, queryWrapper);

        //此时存放的时对于数据的编号，需要转化为对于的值
        userInfoPage.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });

        return userInfoPage;
    }

    //锁定用户
    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String, Object> map = new HashMap<>();

        //查询用户信息
        UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
        map.put("userInfo",userInfo);

        //查询改用户下的就诊人信息
        List<Patient> patientList = patientService.findAllUserId(userId);

        map.put("patientList",patientList);

        return map;
    }

    @Override
    public void approval(Long userId, Integer authStatus) {
        //2审核通过，-1审核不通过
        if (authStatus == 2 || authStatus == -1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);

        }
    }

    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理状态
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));

        //用户的状态
        String  statusString = userInfo.getStatus()==0?"锁定":"正常";
        userInfo.getParam().put("statusString", statusString);

        return userInfo;


    }
}
