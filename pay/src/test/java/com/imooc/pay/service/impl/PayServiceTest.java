package com.imooc.pay.service.impl;

import com.imooc.pay.PayApplicationTests;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class PayServiceTest extends PayApplicationTests {
    @Autowired
    private PayService payService;

    @Test
    public void create() {
        String orderId="202002181013";
        BigDecimal amount=new BigDecimal("200");
        payService.Create(orderId, amount, BestPayTypeEnum.ALIPAY_PC);
    }
}