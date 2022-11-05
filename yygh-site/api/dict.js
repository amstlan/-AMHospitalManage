import request from '@/utils/request'

const api_name = `/admin/cmn/dict`

export default{
    //根据DictCode获取下级节点
    findByDictCode(dictCode){
        return request({
            url:`${api_name}/findByDictCode/${dictCode}`,
            method:'get'
        })
    },
    //根据id获取下级节点
    findByParentId(id){
        return request({
            url:`${api_name}/findChilern/${id}`,
            method:'get'
        })
    }
}