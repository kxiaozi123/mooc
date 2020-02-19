package com.imooc.mall.service.impl;

import com.imooc.mall.MallApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class CategoryServiceImplTest extends MallApplicationTests {
    @Autowired
    private CategoryServiceImpl categoryService;

    @Test
    public void findSubCategoryId() {
        Set<Integer> resultSet=new HashSet<>();
        categoryService.findSubCategoryId(100001,resultSet);
        System.out.println(resultSet);

    }
}