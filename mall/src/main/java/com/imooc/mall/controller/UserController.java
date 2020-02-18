package com.imooc.mall.controller;

import com.imooc.mall.domain.User;
import com.imooc.mall.form.UserRegisterForm;
import com.imooc.mall.service.IUserService;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @PostMapping("/register")
    public ResponseVo register(@RequestBody @Valid UserRegisterForm userRegisterForm)
    {
        User user=new User();
        BeanUtils.copyProperties(userRegisterForm,user);
        return userService.register(user);
    }
}
