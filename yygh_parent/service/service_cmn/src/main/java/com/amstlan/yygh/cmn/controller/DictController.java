package com.amstlan.yygh.cmn.controller;

import com.amstlan.yygh.cmn.service.DictSetService;
import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api("数据字典")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {
    @Autowired
    private DictSetService dictSetService;

    //根据id查询子数据

    @GetMapping("/findChilern/{id}")
    @ApiOperation("根据id查询子数据")
    public Result findChilern(@PathVariable Long id){
        List<Dict> dicts = dictSetService.findChilernData(id);
        return Result.ok(dicts);
    }

    //导出数据字典
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response) throws IOException {
        dictSetService.exportDict(response);

    }


    //导入数据字典
    @PostMapping("importData")
    public Result importData(MultipartFile file) throws IOException {
        dictSetService.importDictData(file);
        return Result.ok();
    }

    //根据dictcode和value查询
    @GetMapping("/getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        String dictName = dictSetService.getDictName(dictCode,value);
        return dictName;
    }


    //根据value查询
    //根据dictcode和value查询
    @GetMapping("/getName/{value}")
    public String getName(@PathVariable String value){
        String dictName = dictSetService.getDictName(null,value);
        return dictName;
    }

    //根据dictcode查询所有的省
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        List<Dict> list = dictSetService.findByDictCode(dictCode);
        return Result.ok(list);
    }
}
