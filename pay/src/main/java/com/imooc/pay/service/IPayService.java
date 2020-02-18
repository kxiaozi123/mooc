package com.imooc.pay.service;

import com.imooc.pay.domain.PayInfo;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;

import java.math.BigDecimal;

public interface IPayService {
    //支付
    PayResponse Create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

    //查询订单
    PayInfo queryByOrderId(String orderId);

    //异步通知处理
    String asyncNotify(String notifyData);
}
