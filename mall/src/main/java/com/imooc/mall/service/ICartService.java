package com.imooc.mall.service;

import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;

public interface ICartService {
    ResponseVo<CartVo> list(Integer uid);
    ResponseVo<CartVo> add(Integer uid, CartAddForm cartAddForm);
    ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form);
    ResponseVo<CartVo> delete(Integer uid,Integer productId);
    ResponseVo<CartVo> selectAll(Integer uid);
    ResponseVo<CartVo> unSelectAll(Integer uid);
    ResponseVo<Integer> sum(Integer uid);
}
