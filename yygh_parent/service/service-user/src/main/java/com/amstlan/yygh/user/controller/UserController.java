package com.amstlan.yygh.user.controller;

import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.model.user.UserInfo;
import com.amstlan.yygh.user.service.UserInfoService;
import com.amstlan.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    //用户列表（条件查询，分页
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit, UserInfoQueryVo userInfoQueryVo){

        Page<UserInfo> infoPage = new Page<>(page,limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(infoPage, userInfoQueryVo);
        return Result.ok(pageModel);
    }

    //用户锁定
    @PutMapping("/lock/{userId}/{status}")
    public Result lock(@PathVariable Long userId, @PathVariable Integer status){
        userInfoService.lock(userId,status);
        return Result.ok();
    }


    //根据用户id查询用户详细信息
    @GetMapping("/show/{userId}")
    public Result show(@PathVariable Long userId){
        Map<String,Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }

    //审批用户信息
    @GetMapping("/approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId, @PathVariable Integer authStatus){
        System.out.println(userId +authStatus);
        userInfoService.approval(userId, authStatus);
        return  Result.ok();
    }

}
