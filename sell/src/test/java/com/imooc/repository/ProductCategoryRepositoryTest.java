package com.imooc.repository;
import com.imooc.SellApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
public class ProductCategoryRepositoryTest extends SellApplicationTests {
    @Autowired
    private ProductCategoryRepository repository;
    @Test
    public void findOne() {
        System.out.println(repository.findOne(1));
    }
}
