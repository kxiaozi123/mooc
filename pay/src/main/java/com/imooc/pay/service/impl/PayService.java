package com.imooc.pay.service.impl;

import com.imooc.pay.dao.PayInfoMapper;
import com.imooc.pay.domain.PayInfo;
import com.imooc.pay.enums.PayPlatformEnum;
import com.imooc.pay.service.IPayService;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
@Slf4j
public class PayService implements IPayService {
    @Resource
    private PayInfoMapper payInfoMapper;
    @Autowired
    private BestPayService bestPayService;

    @Override
    public PayResponse Create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {
        //写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount);
        payInfoMapper.insertSelective(payInfo);
        PayRequest payRequest = new PayRequest();
        payRequest.setOrderId(orderId);
        payRequest.setOrderName("4559066-最好的支付sdk");
        payRequest.setOrderAmount(amount.doubleValue());
        payRequest.setPayTypeEnum(bestPayTypeEnum);
        PayResponse response = bestPayService.pay(payRequest);
        log.info("发起支付 response={}", response);
        return response;

    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }

    @Override
    public String asyncNotify(String notifyData) {
        //1. 签名检验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 response={}", payResponse);

        //2. 金额校验（从数据库查订单）
        //比较严重（正常情况下是不会发生的）发出告警：钉钉、短信
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if (payInfo == null) {
            //告警
            throw new RuntimeException("通过orderNo查询到的结果是null");
        }
        //如果订单支付状态不是"已支付"
        if (!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())) {
            //Double类型比较大小，精度。1.00  1.0
            if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0) {
                //告警
                throw new RuntimeException("异步通知中的金额和数据库里的不一致，orderNo=" + payResponse.getOrderId());
            }

            //3. 修改订单支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }

        //TODO pay发送MQ消息，mall接受MQ消息

        if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            //4. 告诉微信不要再通知了
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        } else if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            return "success";
        } else {
            throw new RuntimeException("异步通知中错误的支付平台");
        }
    }
}
