package com.amstlan.yygh.hosp.controller;

import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.common.util.MD5;
import com.amstlan.yygh.hosp.service.HospitalSetService;
import com.amstlan.yygh.model.hosp.HospitalSet;
import com.amstlan.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //1.查询所有数据
    @ApiOperation(value = "获取医院设置")
    @GetMapping("/findAll")
    public Result findAllHospitalSet(){
        List<HospitalSet> hospitalSetList = hospitalSetService.list();
        return Result.ok(hospitalSetList);
    }

    //2.删除
    @ApiOperation(value = "删除医院")
    @DeleteMapping("/{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean b = hospitalSetService.removeById(id);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }

    }

    //3.条件查询分页
    @PostMapping("/findPage/{current}/{limit}")
    //当前页数，每页个数，封装魏对象的条件
    public Result findPageHospital(@PathVariable long current,
                                   @PathVariable long limit,
                                   @RequestBody HospitalSetQueryVo hospitalQueryVo){
        Page<HospitalSet> pages = new Page<>(current,limit);

        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();


        String hosname = hospitalQueryVo.getHosname();
        String hoscode = hospitalQueryVo.getHoscode();

        if (!StringUtils.isEmpty(hosname)){
            queryWrapper.like("hosname",hospitalQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hoscode)){
            queryWrapper.eq("hoscode",hospitalQueryVo.getHoscode());
        }
        Page<HospitalSet> pageHospital = hospitalSetService.page(pages, queryWrapper);

        return Result.ok(pageHospital);
    }

    //4.添加医院设置
    @PostMapping("saveHospital")
    public Result saveHospital(@RequestBody HospitalSet hospitalSet){
        //状态,1能用，0不能用
        hospitalSet.setStatus(1);

        //密钥
        Random random = new Random();
        //MD5加密
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        //存储
        boolean save = hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else {
            return Result.fail();
        }

    }


    //5.根据id获取医院设置
    @GetMapping("/getHospitalById/{id}")
    public Result getHospitalById(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    //6.修改医院设置
    @PostMapping("/updateHospital")
    public Result updateHospital(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if(flag){
            return Result.ok();
        }
        else {
            return Result.fail();
        }

    }


    //7.批量删除
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospital(@RequestBody List<String> idList){
        boolean flag = hospitalSetService.removeByIds(idList);
        if(flag){
            return Result.ok();
        }
        else {
            return Result.fail();
        }
    }

    //8.医院锁定与解锁
    @PutMapping("lockHospital/{id}/{status}")
    public Result lockHospital(@PathVariable Long id,
                               @PathVariable Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if(flag){
            return Result.ok();
        }
        else {
            return Result.fail();
        }
    }

    //9.发送密钥
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();

        return Result.ok();
    }


}
