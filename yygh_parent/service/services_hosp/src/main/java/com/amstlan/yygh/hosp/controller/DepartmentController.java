package com.amstlan.yygh.hosp.controller;

import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.hosp.service.DepartmentService;
import com.amstlan.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")

public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    //根据医院编号查询里面所有的科室
    @GetMapping("/getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }

}
