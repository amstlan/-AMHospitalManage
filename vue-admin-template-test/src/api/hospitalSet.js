import request from '@/utils/request'

export function login(username, password) {
  return request({
    url: '/user/login',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

export default {
  //医院设置列表
  getHospitalSetList(current,limit,searchObj){
    return request({
      url:`/admin/hosp/hospitalSet/findPage/${current}/${limit}/`,
      method:'post',
      data: searchObj
    })
  },
  //删除
  deleteHospitalSet(id){
    return request({
      url:`/admin/hosp/hospitalSet/${id}/`,
      method:'delete'
    })
  },
  //批量删除
  batchHospital(idList){
    return request({
      url:`/admin/hosp/hospitalSet/batchRemove`,
      method:'delete',
      data: idList
    })
  },

  //锁定和取消锁定
  lockHospital(id,status){
    return request({
      url:`/admin/hosp/hospitalSet/lockHospital/${id}/${status}`,
      method:'put'

    })
  },
  //添加医院
  saveHospital(hospitalSet){
    return request({
      url:`/admin/hosp/hospitalSet/saveHospital`,
      method:'post',
      data: hospitalSet

    })
  },


  //医院id查询
  getHospSet(id){
    return request({
      url:`/admin/hosp/hospitalSet/getHospitalById/${id}`,
      method:'get',

    })
  },
  updateHospital(hospitalSet){
    return request({
      url:`/admin/hosp/hospitalSet/updateHospital`,
      method:'post',
      data: hospitalSet

    })
  }

}