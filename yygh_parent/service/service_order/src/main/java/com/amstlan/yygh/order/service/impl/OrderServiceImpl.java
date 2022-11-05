package com.amstlan.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.amstlan.common.rabbit.constent.MqConst;
import com.amstlan.common.rabbit.service.RabbitService;
import com.amstlan.yygh.common.exception.YyghException;
import com.amstlan.yygh.common.helper.HttpRequestHelper;
import com.amstlan.yygh.common.result.ResultCodeEnum;
import com.amstlan.yygh.enums.OrderStatusEnum;
import com.amstlan.yygh.hosp.client.HospitalFeignClient;
import com.amstlan.yygh.model.order.OrderInfo;
import com.amstlan.yygh.model.user.Patient;
import com.amstlan.yygh.model.user.UserInfo;
import com.amstlan.yygh.order.mapper.OrderMapper;
import com.amstlan.yygh.order.service.OrderService;
import com.amstlan.yygh.vo.hosp.ScheduleOrderVo;
import com.amstlan.yygh.vo.msm.MsmVo;
import com.amstlan.yygh.vo.order.OrderMqVo;
import com.amstlan.yygh.vo.order.OrderQueryVo;
import com.amstlan.yygh.vo.order.SignInfoVo;
import com.amstlan.yygh.user.client.PatientFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class
OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //患者信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);

        //获取排班信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

        //判断十分可以预约
        if(new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.TIME_NO);
        }

        //获取签名信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());

        //数据添加到订单表
        OrderInfo orderInfo = new OrderInfo();
        //主要数据就在scheduleOrderVo里面
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        //向里面设置其他的值
        String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100); //订单号，随机生成一下
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(orderInfo);

        //调用医院接口下单
        //设置一些需要的参数

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("depcode",orderInfo.getDepcode());
        paramMap.put("hosScheduleId",orderInfo.getScheduleId());
        paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount",orderInfo.getAmount());
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        System.out.println(sign);
        paramMap.put("sign", sign);

        //请求医院系统接口
        //TODO 感觉这里不太对
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");

        System.out.println(signInfoVo.getApiUrl());
        System.out.println(result);
        if (result.getInteger("code") == 200){
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");;
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);

            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //TODO 发送mq信息更新号源和短信通知
            //发送mq进行号源的更新
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);

            //进行短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);

            orderMqVo.setMsmVo(msmVo);
            //发消息
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);

        }else {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        System.out.println("orderInfo.getId()-->"+orderInfo.getId());
        return orderInfo.getId();
    }

    @Override
    public OrderInfo getOrder(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);

        System.out.println("order--->"+orderInfo);
        return this.packOrderInfo(orderInfo);
    }

    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> infoPage, OrderQueryVo orderQueryVo) {

        //获取条件值
        //用户名称
        String keyword = orderQueryVo.getKeyword();
        //就诊人名称
        Long patientId = orderQueryVo.getPatientId();
        //订单状态
        String orderStatus = orderQueryVo.getOrderStatus();
        //安排时间
        String reserveDate = orderQueryVo.getReserveDate();
        //开始时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        //结束时间
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        //对条件值进行判断
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(keyword)){
            queryWrapper.like("hosname",keyword);
        }
        if(!StringUtils.isEmpty(patientId)){
            queryWrapper.eq("patient_id",patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)){
            queryWrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)){
            queryWrapper.eq("reserve_date",reserveDate);
        }


        if(!StringUtils.isEmpty(createTimeBegin)){
            queryWrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.le("create_time",createTimeEnd);
        }

        IPage<OrderInfo> orderInfoIPage = baseMapper.selectPage(infoPage, queryWrapper);

        //此时存放的时对于数据的编号，需要转化为对于的值
        orderInfoIPage.getRecords().stream().forEach(item -> {
            this.packOrderInfo(item);
        });

        return orderInfoIPage;
    }

    @Override
    public Object show(Long orderId) {
        Map<String, Object> map = new HashMap<>();
        OrderInfo orderInfo = this.packOrderInfo(this.getById(orderId));
        map.put("orderInfo", orderInfo);
        Patient patient
                =  patientFeignClient.getPatientOrder(orderInfo.getPatientId());
        map.put("patient", patient);
        return map;
    }

    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
}
