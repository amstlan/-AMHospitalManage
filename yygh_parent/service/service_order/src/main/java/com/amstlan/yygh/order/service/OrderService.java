package com.amstlan.yygh.order.service;

import com.amstlan.yygh.model.order.OrderInfo;
import com.amstlan.yygh.model.user.UserInfo;
import com.amstlan.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<OrderInfo> {
    Long saveOrder(String scheduleId, Long patientId);

    OrderInfo getOrder(String scheduleId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> infoPage, OrderQueryVo orderQueryVo);

    Object show(Long id);
}
