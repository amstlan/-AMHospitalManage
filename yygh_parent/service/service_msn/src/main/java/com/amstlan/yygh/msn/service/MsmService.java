package com.amstlan.yygh.msn.service;

import com.amstlan.yygh.vo.msm.MsmVo;

public interface MsmService {
    boolean send(String phone,String code);

    //专门给mq试试
    boolean send(MsmVo msmVo);

}
