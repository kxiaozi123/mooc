package com.imooc.mall.service.impl;

import com.google.gson.Gson;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.domain.Cart;
import com.imooc.mall.domain.Product;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.service.ICartService;
import com.imooc.mall.utils.RedisUtil;
import com.imooc.mall.vo.CartProductVo;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.imooc.mall.enums.ProductStatusEnum.ON_SALE;
import static com.imooc.mall.enums.ResponseEnum.*;

@Service
public class CartServiceImpl implements ICartService {
    @Resource
    private ProductMapper productMapper;

    @Autowired
    private RedisUtil redisUtil;

    private final static String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    private Gson gson = new Gson();

    @Override
    public ResponseVo<CartVo> add(Integer uid, CartAddForm cartAddForm) {
        //购物车数量+1
        Integer quantity=1;

        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        Product product = productMapper.selectByPrimaryKey(cartAddForm.getProductId());
        //商品是否存在
        if (product == null) {
            return ResponseVo.error(PRODUCT_NOT_EXIST);
        }
        //商品是否正常在售
        if(!product.getStatus().equals(ON_SALE.getCode()))
        {
            return ResponseVo.error(PRODUCT_OFF_SALE_OR_DELETE);
        }
        if(product.getStock()<=0)
        {
            return ResponseVo.error(PROODUCT_STOCK_ERROR);
        }
        String value = redisUtil.hget(redisKey, String.valueOf(product.getId()));
        Cart cart;
        if(StringUtils.isEmpty(value))
        {
             cart = new Cart(product.getId(), quantity, cartAddForm.getSelected());
        }
        else {
            cart  = gson.fromJson(value, Cart.class);
            cart.setQuantity(cart.getQuantity()+quantity);
        }
        redisUtil.hset(redisKey,String.valueOf(product.getId()),gson.toJson(cart));
        return list(uid);
    }
    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        List<CartProductVo> cartProductVoList=new ArrayList<>();
        CartVo cartVo=new CartVo();
        //总价
        BigDecimal cartTotalPrice = BigDecimal.ZERO;
        boolean selectAll = true;
        Integer cartTotalQuantity = 0;

        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> map = redisUtil.getMap(redisKey);
        List<Product> productList=new ArrayList<>();
        if(!map.keySet().isEmpty())
        {
            //获取商品id的集合
            productList = productMapper.selectByProductIdSet(map.keySet());
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
            for (Product product : productList) {
                if(cart.getProductId().equals(product.getId()))
                {
                    CartProductVo cartProductVo = new CartProductVo(cart.getProductId(),
                            cart.getQuantity(),
                            product.getName(),
                            product.getSubtitle(),
                            product.getMainImage(),
                            product.getPrice(),
                            product.getStatus(),
                            product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                            product.getStock(),
                            cart.getProductSelected()
                    );
                    cartProductVoList.add(cartProductVo);
                    if (!cart.getProductSelected()) {
                        selectAll = false;
                    }
                    //计算总价(只计算选中的)
                    if (cart.getProductSelected()) {
                        cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
                    }
                }
            }
            cartTotalQuantity += cart.getQuantity();
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setSelectedAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        System.out.println(gson.toJson(cartVo));
        return ResponseVo.success(cartVo);
    }
    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form) {
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = redisUtil.hget(redisKey, String.valueOf(productId));
        if(StringUtils.isEmpty(value))
        {
            //没有该商品, 报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        //已经有了，修改内容
        Cart cart = gson.fromJson(value, Cart.class);
        if (form.getQuantity() != null
                && form.getQuantity() >= 0) {
            cart.setQuantity(form.getQuantity());
        }
        if (form.getSelected() != null) {
            cart.setProductSelected(form.getSelected());
        }
        redisUtil.hset(redisKey,String.valueOf(productId),gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = redisUtil.hget(redisKey, String.valueOf(productId));
        if(StringUtils.isEmpty(value))
        {
            //没有该商品, 报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        redisUtil.delete(redisKey,String.valueOf(productId));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        selectAllOrUnSelectAll(uid,true);
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        selectAllOrUnSelectAll(uid,false);
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Integer sum = listForCart(redisKey).stream()
                .map(Cart::getQuantity)
               .reduce(0,Integer::sum);
        return ResponseVo.success(sum);
    }

    private List<Cart> listForCart(String redisKey)
    {
        List<Cart> cartList=new ArrayList<>();
        Map<String, String> map = redisUtil.getMap(redisKey);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
            cartList.add(cart);
        }
        return cartList;
    }
    private void selectAllOrUnSelectAll (Integer uid,Boolean flag)
    {
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        List<Cart> cartList = listForCart(redisKey);
        for (Cart cart : cartList) {
            cart.setProductSelected(flag);
            redisUtil.hset(redisKey,String.valueOf(cart.getProductId()),gson.toJson(cart));
        }
    }


}
