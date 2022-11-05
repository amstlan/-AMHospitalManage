package com.amstlan.yygh.cmn.service;

import com.amstlan.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface DictSetService extends IService<Dict> {
    List<Dict> findChilernData(Long id);

    void exportDict(HttpServletResponse response) throws IOException;

    void importDictData(MultipartFile file) throws IOException;

    String getDictName(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
