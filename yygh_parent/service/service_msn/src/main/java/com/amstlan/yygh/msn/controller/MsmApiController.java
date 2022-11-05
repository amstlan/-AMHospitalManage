package com.amstlan.yygh.msn.controller;

import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.msn.service.MsmService;
import com.amstlan.yygh.msn.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmApiController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/send/{phone}")
    public Result sendCode(@PathVariable String phone){
        //查看redis里面是否有验证码
        //key是手机号，value是验证码
        String code = (String) redisTemplate.opsForValue().get(phone);

        if (!StringUtils.isEmpty(code)){
            return Result.ok();
        }

        //没有就创建，并进行设置
        code = RandomUtil.getSixBitRandom();
        boolean isSend = msmService.send(phone,code);

        if (isSend){
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            return Result.ok();
        }else {
            return Result.fail().message("发送短信失败");
        }
    }
}
