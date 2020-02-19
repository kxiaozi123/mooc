package com.imooc.mall.controller;

import com.imooc.mall.consts.MallConst;
import com.imooc.mall.domain.User;
import com.imooc.mall.form.UserLoginForm;
import com.imooc.mall.form.UserRegisterForm;
import com.imooc.mall.service.IUserService;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class UserController {
    @Autowired
    private IUserService userService;
    @PostMapping("/user/register")
    public ResponseVo register(@RequestBody @Valid UserRegisterForm userRegisterForm)
    {
        User user=new User();
        BeanUtils.copyProperties(userRegisterForm,user);
        return userService.register(user);
    }
    @PostMapping("/user/login")
    public ResponseVo login(@RequestBody @Valid UserLoginForm userLoginForm, HttpSession session)
    {
        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
        session.setAttribute(MallConst.CURRENT_USER, userResponseVo.getData());
        return userResponseVo;
    }
    @GetMapping("/user")
    public ResponseVo user( HttpSession session)
    {
         User user= (User) session.getAttribute(MallConst.CURRENT_USER);
         return ResponseVo.success(user);
    }
    @PostMapping("/user/logout")
    public ResponseVo logout(HttpSession session) {
        session.removeAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success();
    }
}
