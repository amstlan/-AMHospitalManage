package com.amstlan.yygh.hosp.service;

import com.amstlan.yygh.model.hosp.HospitalSet;
import com.amstlan.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);

    SignInfoVo getSignInfoVo(String hoscode);
}
