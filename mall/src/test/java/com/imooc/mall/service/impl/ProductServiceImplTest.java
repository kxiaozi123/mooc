package com.imooc.mall.service.impl;

import com.imooc.mall.MallApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ProductServiceImplTest extends MallApplicationTests {
    @Autowired
    private ProductServiceImpl productService;

    @Test
    public void list() {
        productService.list(null,1,1);
    }
}