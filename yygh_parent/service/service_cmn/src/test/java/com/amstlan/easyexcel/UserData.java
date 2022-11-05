package com.amstlan.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserData {

    @ExcelProperty("用户编号")
    private int uid;

    @ExcelProperty("用户名称")
    private String username;

}
