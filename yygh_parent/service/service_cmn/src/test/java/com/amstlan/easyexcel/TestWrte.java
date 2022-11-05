package com.amstlan.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestWrte {
    public static void main(String[] args) {
        String url = "D:\\project\\exl\\01.xlsx";

        List<UserData> userDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserData u = new UserData();
            u.setUid(i);
            u.setUsername("zhang"+i);
            userDataList.add(u);
        }

        EasyExcel.write(url,UserData.class).sheet("用户信息")
                .doWrite(userDataList);
    }
}
