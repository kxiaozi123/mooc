package com.imooc.pay.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BestPayConfig {
    @Autowired
    private AlipayAccountConfig alipayAccountConfig;
    @Autowired
    private WxAccountConfig wxAccountConfig;

    @Bean
    public BestPayService bestPayService(WxPayConfig wxPayConfig,AliPayConfig aliPayConfig)
    {
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setAliPayConfig(aliPayConfig);
        bestPayService.setWxPayConfig(wxPayConfig);
        return bestPayService;
    }
    //设置微信的配置
    @Bean
    public WxPayConfig wxPayConfig()
    {
        WxPayConfig wxPayConfig=new WxPayConfig();
        wxPayConfig.setAppId(wxAccountConfig.getAppId());          //公众号Id
        wxPayConfig.setMchId(wxAccountConfig.getMchId());   //支付商户资料
        wxPayConfig.setMchKey(wxAccountConfig.getMchKey());
        wxPayConfig.setNotifyUrl(wxAccountConfig.getNotifyUrl());
        wxPayConfig.setReturnUrl(wxAccountConfig.getReturnUrl());
        return wxPayConfig;
    }
    //设置支付宝的配置
    @Bean
    public AliPayConfig aliPayConfig()
    {
        AliPayConfig aliPayConfig=new AliPayConfig();
        aliPayConfig.setAppId(alipayAccountConfig.getAppId());
        aliPayConfig.setAliPayPublicKey(alipayAccountConfig.getPublicKey());
        aliPayConfig.setPrivateKey(alipayAccountConfig.getPrivateKey());
        aliPayConfig.setNotifyUrl(alipayAccountConfig.getNotifyUrl());
        aliPayConfig.setReturnUrl(alipayAccountConfig.getReturnUrl());
        return aliPayConfig;
    }
}
