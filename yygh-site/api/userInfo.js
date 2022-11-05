import request from '@/utils/request'

const api_name = `/api/user`

export default {
    //手机登录接口，微信登录是直接交给微信平台，sbtx
    login(userInfo) {
        return request({
            url: `${api_name}/login`,
            method: `post`,
            data: userInfo
        })
    },

    //用户认证
    saveUserAuah(userAuah) {
        return request({
          url: `${api_name}/auth/userAuth`,
          method: 'post',
          data: userAuah
        })
    },

      //根据用户id获取用户信息
      getUserInfo() {
        return request({
          url: `${api_name}/auth/getUserInfo`,
          method: `get`
        })
    },
        
      



}
