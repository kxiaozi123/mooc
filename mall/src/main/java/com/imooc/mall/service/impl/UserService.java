package com.imooc.mall.service.impl;

import com.imooc.mall.dao.UserMapper;
import com.imooc.mall.domain.User;
import com.imooc.mall.enums.RoleEnum;
import com.imooc.mall.service.IUserService;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

import static com.imooc.mall.enums.ResponseEnum.*;

@Service
public class UserService implements IUserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public ResponseVo register(User user) {
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if(countByUsername>0)
        {
            return ResponseVo.error(USERNAME_EXIST);
        }
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if(countByEmail>0)
        {
            return ResponseVo.error(EMAIL_EXIST);
        }
        user.setRole(RoleEnum.CUSTOMER.getCode());
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        int resultCount = userMapper.insertSelective(user);
        if(resultCount<0)
        {
            return  ResponseVo.error(ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        return null;
    }


}
