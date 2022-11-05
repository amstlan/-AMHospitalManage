package com.amstlan.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.amstlan.yygh.cmn.client.DictFeignClient;
import com.amstlan.yygh.common.exception.YyghException;
import com.amstlan.yygh.common.result.ResultCodeEnum;
import com.amstlan.yygh.hosp.repository.HospitalRepository;
import com.amstlan.yygh.hosp.service.HospitalService;
import com.amstlan.yygh.model.hosp.Hospital;
import com.amstlan.yygh.model.hosp.HospitalSet;
import com.amstlan.yygh.vo.hosp.HospitalQueryVo;
import com.amstlan.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> paramMap) {
        //转换对象
        String jsonString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);

        //判断数据库中是否有重复数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExi = hospitalRepository.getHospitalByHoscode(hoscode);

        if (hospitalExi != null){ //存在则修改
            hospital.setId(hospitalExi.getId());
            hospital.setStatus(hospitalExi.getStatus());
            hospital.setCreateTime(hospitalExi.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else { //不存在添加
            System.out.println("无数据");
            hospital.setStatus(0);

            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }


    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospitalByHoscode = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospitalByHoscode;
    }

    @Override
    public Page selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {

        //创建
        Pageable pageable = PageRequest.of(page - 1,limit);

        //
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);

        Example<Hospital> example = Example.of(hospital,matcher);

        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        List<Hospital> content = pages.getContent();

        pages.getContent().stream().forEach(item->{
            this.setHospitalHosType(item);
        });

        return pages;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();

        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String, Object>  getHsopById(String id) {
        Map<String, Object> map = new HashMap<>();
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        map.put("hospital",hospital);
        map.put("bookingRule",hospital.getBookingRule());
        hospital.setBookingRule(null);
        return map;
    }

    @Override
    public String getHosName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospital != null){
            return hospital.getHosname();
        }
        return null;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        //预约规则
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;

    }




    public Hospital setHospitalHosType(Hospital hospital){
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());

        String provinceNane = dictFeignClient.getName(hospital.getProvinceCode());
        String cityNane = dictFeignClient.getName(hospital.getCityCode());
        String districtNane = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("fullAddress",provinceNane+cityNane+districtNane);
        hospital.getParam().put("hostypeString",hostypeString);
        return hospital;
    }


}
