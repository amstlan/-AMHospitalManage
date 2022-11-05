package com.amstlan.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.amstlan.yygh.hosp.repository.DepartmentRepository;
import com.amstlan.yygh.hosp.service.DepartmentService;
import com.amstlan.yygh.model.hosp.Department;
import com.amstlan.yygh.vo.hosp.DepartmentQueryVo;
import com.amstlan.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);

        //查询科室信息
        Department departmentExist =
            departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        //判断
        if (departmentExist != null){
            departmentExist .setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else {
            department.setUpdateTime(new Date());
            department.setCreateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建Pageable对象，当前页和每页记录数

        Pageable pageable = PageRequest.of(page - 1,limit);

        //创建Example对象
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);

        Example<Department> example = Example.of(department, exampleMatcher);

        Page<Department> all = departmentRepository.findAll(example, pageable);

        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null){
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合，封装最终数据
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号查询所有的科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> example = Example.of(departmentQuery);
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号 bigcode 进行分组
        Map<String, List<Department>> departmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        //遍历上面的map
        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()){
            String bigcode = entry.getKey();

            //大科室对应的所有科室
            List<Department> depList = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigcode);
            departmentVo.setDepname(depList.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> chilhren = new ArrayList<>();
            depList.forEach(item -> {
                DepartmentVo vo = new DepartmentVo();
                vo.setDepcode(item.getDepcode());
                vo.setDepname(item.getDepname());
                chilhren.add(vo);
            });

            departmentVo.setChildren(chilhren);

            result.add(departmentVo);
        }

        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null ){
            return department.getDepname();
        }
        return null;
    }

    //根据医院编号和科室编号，获取科室
    @Override
    public Department getDepartment(String hoscode, String depcode) {

        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }
}
