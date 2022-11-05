package com.amstlan.yygh.hosp.service;

import com.amstlan.yygh.model.hosp.Hospital;
import com.amstlan.yygh.vo.hosp.HospitalQueryVo;
import com.amstlan.yygh.vo.order.SignInfoVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {


    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    Page selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Map<String, Object>  getHsopById(String id);

    String getHosName(String hoscode);

    List<Hospital> findByHosname(String hosname);

    Map<String, Object> item(String hoscode);

}
