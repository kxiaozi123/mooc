package com.imooc.mall.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
@Slf4j
public class CartServiceImplTest extends MallApplicationTests {
    @Autowired
    private CartServiceImpl cartService;
    private Gson gson=new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void add() {
        CartAddForm cartAddForm=new CartAddForm();
        cartAddForm.setProductId(29);
        cartAddForm.setSelected(true);
        ResponseVo<CartVo> add = cartService.add(12345, cartAddForm);
        log.info("add={}",gson.toJson(add));
    }
    @Test
    public void list()
    {
        ResponseVo<CartVo> list = cartService.list(12345);
        log.info("list={}",gson.toJson(list));
    }

    @Test
    public void update() {
        CartUpdateForm cartUpdateForm=new CartUpdateForm();
        //cartUpdateForm.setQuantity(3);
        cartUpdateForm.setSelected(false);
        ResponseVo<CartVo> update = cartService.update(12345, 26, cartUpdateForm);
        log.info("update={}",gson.toJson(update));
    }

    @Test
    public void delete() {
        ResponseVo<CartVo> delete = cartService.delete(12345, 26);
        log.info("delete={}",gson.toJson(delete));
    }

    @Test
    public void selectAll() {
        ResponseVo<CartVo> selectAll = cartService.selectAll(12345);
        log.info("selectAll={}",gson.toJson(selectAll));
    }

    @Test
    public void unSelectAll() {
        ResponseVo<CartVo> unSelectAll = cartService.unSelectAll(12345);
        log.info("unSelectAll={}",gson.toJson(unSelectAll));
    }

    @Test
    public void sum() {
        ResponseVo<Integer> sum = cartService.sum(12345);
        log.info("sum={}",gson.toJson(sum));
    }
}