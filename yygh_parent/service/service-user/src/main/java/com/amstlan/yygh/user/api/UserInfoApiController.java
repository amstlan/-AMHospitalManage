package com.amstlan.yygh.user.api;

import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.common.utils.AuthContextHolder;
import com.amstlan.yygh.model.user.UserInfo;
import com.amstlan.yygh.user.service.UserInfoService;
import com.amstlan.yygh.vo.user.LoginVo;
import com.amstlan.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;


    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String, Object> map = userInfoService.loginUser(loginVo);
        return Result.ok(map);
    }

    //用户认证接口
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        //需要用户的id，认证数据的vo对象
        userInfoService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);

        return Result.ok();
    }

    //获取用户id
    @GetMapping("/auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }

}
