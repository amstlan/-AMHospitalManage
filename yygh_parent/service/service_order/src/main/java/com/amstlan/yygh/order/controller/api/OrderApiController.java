package com.amstlan.yygh.order.controller.api;

import com.amstlan.yygh.common.result.Result;
import com.amstlan.yygh.common.utils.AuthContextHolder;
import com.amstlan.yygh.enums.OrderStatusEnum;
import com.amstlan.yygh.model.order.OrderInfo;
import com.amstlan.yygh.model.user.UserInfo;
import com.amstlan.yygh.order.service.OrderService;
import com.amstlan.yygh.vo.order.OrderQueryVo;
import com.amstlan.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Autowired
    private OrderService orderService;



    //生成挂号订单
    @PostMapping("/auth/submitOrder/{scheduleId}/{patientId}")
    public Result submitOrder(
            @PathVariable String scheduleId,
            @PathVariable Long patientId) {
        Long order = orderService.saveOrder(scheduleId, patientId);
        return Result.ok(order);

    }

    //根据订单id查询订单详情
    @GetMapping("/auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId){
        OrderInfo orderInfo = orderService.getOrder(orderId);
        System.out.println("--->"+orderId);
        return Result.ok(orderInfo);
    }

    //订单列表
    @GetMapping("/auth/{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit, OrderQueryVo orderQueryVo, HttpServletRequest request){

        //设置当前用户id
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> infoPage = new Page<>(page,limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(infoPage, orderQueryVo);
        return Result.ok(pageModel);
    }

    //查询订单状态
    @GetMapping("/auth/getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }
}
