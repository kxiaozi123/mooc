package com.imooc.mall.service.impl;

import com.imooc.mall.consts.MallConst;
import com.imooc.mall.dao.CategoryMapper;
import com.imooc.mall.domain.Category;
import com.imooc.mall.service.ICategoryService;
import com.imooc.mall.vo.CategoryVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Resource
    private CategoryMapper mapper;

    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        List<Category> categories = mapper.selectAll();
        List<CategoryVo> categoryVoList = categories.stream()
                .map(this::category2CategoryVo)
                .filter(e->e.getParentId().equals(MallConst.ROOT_PARENT_ID))
                .sorted(Comparator.comparing(CategoryVo::getSortOrder).reversed())
                .collect(Collectors.toList());
        findSubCategory(categoryVoList,categories);
        return ResponseVo.success(categoryVoList);


    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categoryList = mapper.selectAll();
        findSubCategoryId(id,resultSet,categoryList);


    }
    private void findSubCategoryId(Integer id, Set<Integer> resultSet,List<Category> categoryList)
    {
        for (Category category : categoryList) {
            if(category.getParentId().equals(id))
            {
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(),resultSet,categoryList);
            }
        }
    }

    public CategoryVo category2CategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }

    public void findSubCategory(List<CategoryVo> categoryVoList, List<Category> categories) {
        for (CategoryVo categoryVo : categoryVoList) {
            List<CategoryVo> subCategories=new ArrayList<>();
            for (Category category : categories) {
                if(categoryVo.getId().equals(category.getParentId()))
                {
                    CategoryVo subCategory = category2CategoryVo(category);
                    subCategories.add(subCategory);
                }
                subCategories.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
                categoryVo.setSubCategories(subCategories);
                findSubCategory(subCategories,categories);
            }
        }
    }

}
