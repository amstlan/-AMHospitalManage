package com.amstlan.yygh.hosp.controller.api;

import com.amstlan.yygh.common.exception.YyghException;
import com.amstlan.yygh.common.helper.HttpRequestHelper;
import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.common.result.ResultCodeEnum;
import com.amstlan.yygh.common.util.MD5;
import com.amstlan.yygh.hosp.service.DepartmentService;
import com.amstlan.yygh.hosp.service.HospitalService;
import com.amstlan.yygh.hosp.service.HospitalSetService;
import com.amstlan.yygh.hosp.service.ScheduleService;
import com.amstlan.yygh.model.hosp.Department;
import com.amstlan.yygh.model.hosp.Hospital;
import com.amstlan.yygh.model.hosp.Schedule;
import com.amstlan.yygh.vo.hosp.DepartmentQueryVo;
import com.amstlan.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;



    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取到传过来的签名，进行MD5加密
        String hospSign = (String) paramMap.get("sign");

        //根据传递过来的编码，查询出对于的数据的签名
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);

        String signKeyMD5 = MD5.encrypt(signKey);


        if (!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.save(paramMap);
        return Result.ok();
    }

    //上传医院接口
    @PostMapping("/saveHospital")
    public Result saveHosp(HttpServletRequest request){
        //获取到医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取到传过来的签名，进行MD5加密
        String hospSign = (String) paramMap.get("sign");

        //根据传递过来的编码，查询出对于的数据的签名
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);

        String signKeyMD5 = MD5.encrypt(signKey);


        if (!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        String logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ","+");
//        System.out.println("before"+logoData);
        paramMap.put("logoData",logoData);
        String logoData2 = (String) paramMap.get("logoData");
//        System.out.println("after"+logoData2);
        hospitalService.save(paramMap);
        return Result.ok();


    }


    @PostMapping("hospital/show")
    public Result hospitalShow(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String hoscode = (String) paramMap.get("hoscode");
        //获取到传过来的签名，进行MD5加密
        String hospSign = (String) paramMap.get("sign");

        //根据传递过来的编码，查询出对于的数据的签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        String signKeyMD5 = MD5.encrypt(signKey);


        if (!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //根据医院编号查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);

        return Result.ok(hospital);

    }


    @PostMapping("department/list")
    public Result departmentList(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String hoscode = (String) paramMap.get("hoscode");
        //医院编号
        int page = StringUtils.isEmpty(paramMap.get("page"))?1:Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit"))?1:Integer.parseInt((String) paramMap.get("limit"));

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        Page<Department> pageModel = departmentService.findPageDepartment(page,limit, departmentQueryVo);


        return Result.ok(pageModel);

    }

    //删除科室
    @PostMapping("department/remove")
    public Result departmentRemove(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");

        departmentService.remove(hoscode,depcode);

        return Result.ok();
    }


    //上传排版接口
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String hoscode = (String) paramMap.get("hoscode");

        scheduleService.save(paramMap);

        return Result.ok();

    }

    //查询排版
    @PostMapping("schedule/list")
    public Result scheduleList(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);


        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        //医院编号
        int page = StringUtils.isEmpty(paramMap.get("page"))?1:Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit"))?1:Integer.parseInt((String) paramMap.get("limit"));

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);

        Page<Schedule> pageModel = scheduleService.findPageSchedule(page,limit, scheduleQueryVo);

        return Result.ok(pageModel);

    }

    //删除排班
    @PostMapping("schedule/remove")
    public Result scheduleRemove(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String hoscode = (String) paramMap.get("hoscode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");

        //前面校验，略

        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }


}
