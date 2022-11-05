package com.amstlan.yygh.hosp.controller;

import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.hosp.service.HospitalService;
import com.amstlan.yygh.model.hosp.Hospital;
import com.amstlan.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")

public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel =  hospitalService.selectHospPage(page,limit,hospitalQueryVo);
        return Result.ok(pageModel);
    }
    //更新医院的上线状态
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,
                                   @PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return Result.ok();

    }

    //医院详情信息
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        Map<String, Object> map = hospitalService.getHsopById(id);
        return Result.ok(map);

    }

}
