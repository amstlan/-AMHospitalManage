import request from '@/utils/request'

export default {
  //医院列表
  getHospList(current,limit,searchObj){
    return request({
      url:`/admin/hosp/hospital/list/${current}/${limit}`,
      method:'get',
      params:searchObj
      
    })
  },
  
  //根据dictcode查询所有子节点，这里是查询所有省市地区
  findByDictCode(dictCode){
    return request({
      url:`/admin/cmn/dict/findByDictCode/${dictCode}`,
      method:'get',
      
    })
  },

  //根据数据id查询子数据列表
  findChilernById(id){
    return request({
      url:`/admin/cmn/dict/findChilern/${id}`,
      method:'get',
      
    })
  },

  //更新医院上线状态
  updateHospStatus(id,status){
    return request({
      url:`/admin/hosp/hospital/updateHospStatus/${id}/${status}`,
      method:'get',
      
    })
  },

  getHospById(id){
    return request({
      url:`/admin/hosp/hospital/showHospDetail/${id}`,
      method:'get',
      
    })
  },
  //查看医院的科室
  getDeptByHoscode(hoscode){
    return request({
      url:`/admin/hosp/department/getDeptList/${hoscode}`,
      method:'get',
      
    })
  },
  getScheduleRule(page,limit,hoscode,depcode){
    return request({
      url:`/admin/hosp/schedule/getScheduleRule/${page}/${limit}/${hoscode}/${depcode}`,
      method:'get',
      
    })
  },
  getScheduleDetial(hoscode,depcode,workDate){
    return request({
      url:`/admin/hosp/schedule/getScheduleDetail/${hoscode}/${depcode}/${workDate}`,
      method:'get',
      
    })
  },

}