package com.amstlan.yygh.msn.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;

import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;

import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.amstlan.yygh.msn.service.MsmService;
import com.amstlan.yygh.msn.utils.ConstantPropertiesUtils;
import com.aliyuncs.exceptions.ClientException;
import com.amstlan.yygh.vo.msm.MsmVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {
    @Override
    public boolean send(String phone,String code) {
        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)){
            return false;
        }
        //一下的代码是设置整合阿里云服务的,结构基本固定，只需要修改参数即可，就是AccessKey这类的玩意
        DefaultProfile profile = DefaultProfile.//加载密钥
                getProfile(ConstantPropertiesUtils.REGION_Id,
                        ConstantPropertiesUtils.ACCESS_KEY_ID,
                        ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        //需要用到https的时候打开
        //request.setProtocol(ProtocolType.HTTPS);
        //跳过
        request.setMethod(MethodType.POST);
        //
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        //内容也是固定的，只需要改键值对的值，键千万不要动
        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", "lanslot");
        //模板code
        request.putQueryParameter("TemplateCode", "SMS_254085483");
        //验证码  使用json格式
        //要么这样用map，要么拼接然后强转 {"code":"123456"}
        Map<String, Object> param = new HashMap<>();
        //code固定，不用管
        param.put("code",code);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));


        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;


    }

    //懒得改上面的方法适应两个调用，就复制改改，大差不差
    public boolean send(String phone,Map<String, Object> param) {
        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)){
            return false;
        }
        //一下的代码是设置整合阿里云服务的,结构基本固定，只需要修改参数即可，就是AccessKey这类的玩意
        DefaultProfile profile = DefaultProfile.//加载密钥
                getProfile(ConstantPropertiesUtils.REGION_Id,
                ConstantPropertiesUtils.ACCESS_KEY_ID,
                ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        //需要用到https的时候打开
        //request.setProtocol(ProtocolType.HTTPS);
        //跳过
        request.setMethod(MethodType.POST);
        //
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        //内容也是固定的，只需要改键值对的值，键千万不要动
        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", "lanslot");
        //模板code
        request.putQueryParameter("TemplateCode", "SMS_254085483");
        //验证码  使用json格式
        //要么这样用map，要么拼接然后强转 {"code":"123456"}

        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));


        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;


    }

    //mq调用短信
    @Override
    public boolean send(MsmVo msmVo) {
        if (!StringUtils.isEmpty(msmVo.getPhone())){
            boolean isSend = this.send(msmVo.getPhone(), msmVo.getParam());
        }

        return false;
    }


    //

}
