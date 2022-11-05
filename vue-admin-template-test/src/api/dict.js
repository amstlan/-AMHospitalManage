import request from '@/utils/request'

export default {
  //医院设置列表
  dictList(id){
    return request({
      url:`/admin/cmn/dict/findChilern/${id}`,
      method:'get',
      
    })
  },
  

}