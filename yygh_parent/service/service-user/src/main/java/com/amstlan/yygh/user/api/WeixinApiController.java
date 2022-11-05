package com.amstlan.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.amstlan.yygh.common.helper.JwtHelper;
import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.model.acl.User;
import com.amstlan.yygh.model.user.UserInfo;
import com.amstlan.yygh.user.service.UserInfoService;
import com.amstlan.yygh.user.utils.ConstantPropertiesUtil;
import com.amstlan.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;

    //微信扫描回调方法
    @GetMapping("callback")
    public String callback(String code,String state){
        System.out.println("code: "+code);
        //请求微信固定地址，拼接参数
        //%s占位符
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);

        try {
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println(accessTokenInfo);
            JSONObject jsonObject = JSONObject.parseObject(accessTokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            //判断数据库是否存在扫描人的信息
            UserInfo userInfo = userInfoService.selectWxInfoOpenId(openid);
            if (userInfo == null){
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println(resultInfo);

                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);


                //解析用户信息
                String nickname = resultUserInfoJson.getString("nickname");
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                //添加到数据库
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);

                userInfoService.save(userInfo);

            }


            //返回name和token字符串
            Map<String, Object> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            return "redirect:" + ConstantPropertiesUtil.YYGH_BASE_URL + "/weixin/callback?token="
                    + map.get("token")+"&openid="
                    + map.get("openid")+"&name="
                    + URLEncoder.encode((String)map.get("name"),"utf-8");

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @GetMapping("/getLoginParam")
    @ResponseBody
    public Result getLoginParam(){
        Map<String, Object> map = new HashMap<>();

        try {
            map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
            map.put("scope","snsapi_login");
            String wxOpenRedirectUrl = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL;
            String encode = URLEncoder.encode(wxOpenRedirectUrl, "utf-8");
            map.put("redirect_uri",encode);
            map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""

            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;

    }
}
