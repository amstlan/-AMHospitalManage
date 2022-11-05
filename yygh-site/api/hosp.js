import request from '@/utils/request'

const api_name = `/api/hosp/hospital`

export default {
    getPageList(page, limit, searchObj) {
        return request({
            url: `${api_name}/findHospList/${page}/${limit}`,
            method: 'get',
            params: searchObj
        })
    },
    //模糊查询
    getByHosname(hosname) {
        return request({
            url: `${api_name}/findByHosName/${hosname}`,
            method: 'get'
        })
    },
    getByHosname(hosname) {
        return request({
            url: `${api_name}/findByHosName/${hosname}`,
            method: 'get'
        })
    },
    //查询医院详情
    show(hoscode) {
        return request({
            url: `${api_name}/findHospDetial/${hoscode}`,
            method: 'get'
        })
    },
    findDepartment(hoscode) {
        return request({
            url: `${api_name}/department/${hoscode}`,
            method: 'get'
        })
    },
    //获取可预约排班数据
    getBookingScheduleRule(page, limit, hoscode, depcode) {
        return request({
            url: `${api_name}/auth/getBookingScheduleRule/${page}/${limit}/${hoscode}/${depcode}`,
            method: 'get'
        })
    },
    //获取排班数据
    findScheduleList(hoscode, depcode, workDate) {
        return request({
            url: `${api_name}/auth/findScheduleList/${hoscode}/${depcode}/${workDate}`,
            method: 'get'
        })
    },

    //根据排班id获取排班信息
    getSchedule(id) {
        return request({
          url: `/api/hosp/hospital/getSchedule/${id}`,
          method: 'get'
        })
    }
}