package com.imooc.pay.controller;

import com.imooc.pay.config.WxAccountConfig;
import com.imooc.pay.domain.PayInfo;
import com.imooc.pay.service.IPayService;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {
    @Autowired
    private IPayService payService;
    @Autowired
    private WxAccountConfig wxAccountConfig;

    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("amount") BigDecimal amount,
                               @RequestParam("payType") BestPayTypeEnum bestPayTypeEnum)
    {
        Map<String, String> map = new HashMap<>();
        if(bestPayTypeEnum==BestPayTypeEnum.WXPAY_NATIVE)
        {
            PayResponse response = payService.Create(orderId, amount, bestPayTypeEnum);
            map.put("codeUrl",response.getCodeUrl());
            map.put("orderId",orderId);
            map.put("returnUrl",wxAccountConfig.getReturnUrl());
            return new ModelAndView("createForWxNative", map);
        }
        else if(bestPayTypeEnum==BestPayTypeEnum.ALIPAY_PC)
        {
            PayResponse response = payService.Create(orderId, amount, bestPayTypeEnum);
            map.put("body",response.getBody());
            return new ModelAndView("createForAlipayPc", map);
        }
        throw new RuntimeException("暂不支持的支付类型");
    }
    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData) {
        return payService.asyncNotify(notifyData);
    }

    @GetMapping("/queryByOrderId")
    @ResponseBody
    public PayInfo queryByOrderId(@RequestParam String orderId) {
        return payService.queryByOrderId(orderId);
    }


}
