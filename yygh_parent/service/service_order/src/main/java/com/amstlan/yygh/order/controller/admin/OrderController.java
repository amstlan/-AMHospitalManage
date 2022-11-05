package com.amstlan.yygh.order.controller.admin;

import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.common.utils.AuthContextHolder;
import com.amstlan.yygh.enums.OrderStatusEnum;
import com.amstlan.yygh.model.order.OrderInfo;
import com.amstlan.yygh.order.service.OrderService;
import com.amstlan.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/admin/order/orderInfo")
public class OrderController {

    @Autowired
    private OrderService orderService;
    //根据订单id查询订单详情
    @GetMapping("/auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId){
        OrderInfo orderInfo = orderService.getOrder(orderId);
        System.out.println("--->"+orderId);
        return Result.ok(orderInfo);
    }

    @GetMapping("{page}/{limit}")
    public Result index(
            @PathVariable Long page,
            @PathVariable Long limit,
            OrderQueryVo orderQueryVo) {
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }


    @GetMapping("getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    @GetMapping("show/{id}")
    public Result get(
            @PathVariable Long id) {
        return Result.ok(orderService.show(id));
    }
}
