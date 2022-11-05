package com.amstlan.yygh.cmn.service.impl;


import com.alibaba.excel.EasyExcel;
import com.amstlan.yygh.cmn.listener.DictListener;
import com.amstlan.yygh.cmn.mapper.DictMapper;
import com.amstlan.yygh.cmn.service.DictSetService;
import com.amstlan.yygh.model.cmn.Dict;
import com.amstlan.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictSetServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictSetService {


    @Autowired
    private DictMapper dictMapper;

    @Override
    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    public List<Dict> findChilernData(Long id) {

        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        dicts.forEach(dict -> {
            Long dictId = dict.getId();
            boolean isChildren = this.isChildren(dictId);
            dict.setHasChildren(isChildren);
        });
        return dicts;
    }

    @Override
    @CacheEvict(value = "dict", allEntries = true)
    public void exportDict(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("数据字典", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        List<Dict> dictList = baseMapper.selectList(null);

        List<DictEeVo> dictEeVoList  = new ArrayList<>();

        dictList.forEach(dict -> {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVoList.add(dictEeVo);
        });

        EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                .doWrite(dictEeVoList);
    }

    //导入数据
    @Override
    public void importDictData(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper))
                .sheet()
                .doRead();
    }

    @Override
    public String getDictName(String dictCode, String value) {

        //判断dictcode是否为空，
        if (StringUtils.isEmpty(dictCode)){
            //为空直接根据value查询
            QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("value", value);
            Dict dict = baseMapper.selectOne(queryWrapper);
            return dict.getName();
        }
        else {
            //不为空，根据两个条件查询
            //根据dictcode查询dict对象
            Dict codeDict = this.getDictByDictCode(dictCode);
            Long parentId = codeDict.getId();
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parentId)
                    .eq("value", value));
            return dict.getName();
        }


    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictCode获取id，根据id、获取子节点
        Dict dict = this.getDictByDictCode(dictCode);
        //获取子节点
        List<Dict> chilernData = this.findChilernData(dict.getId());

        return chilernData;
    }

    private Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        Dict codeDict = baseMapper.selectOne(queryWrapper);
        return codeDict;
    }

    //判断当前id下是否有子数据
    private boolean isChildren(Long id){
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Long count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
