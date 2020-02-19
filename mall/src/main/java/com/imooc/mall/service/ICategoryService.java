package com.imooc.mall.service;

import com.imooc.mall.vo.CategoryVo;
import com.imooc.mall.vo.ResponseVo;

import java.util.List;

/**
 * Created by 廖师兄
 */
public interface ICategoryService {

	ResponseVo<List<CategoryVo>> selectAll();


}
