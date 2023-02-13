package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
//        获取userid，将userid放到购物车数据中，表示是哪个用户点的
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
//        判断是套餐，还是菜品，接着判断之前就添加过吗，添加过只需要使number属性 +1， 每天加过就直接添加进去
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        shoppingCartLambdaQueryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);

//        查询
        ShoppingCart one = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
//        如果 ！= null，说明添加过，number+1
        if (one != null) {
            Integer number = one.getNumber();
            one.setNumber(number + 1);
//            +1之后，update进去
            shoppingCartService.updateById(one);
        }else {
//            说明没添加过，数量设置为1，创建时间设为现在，然后保存，将信息返回给前端，可能要用
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    /**
     * 减少
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody Map map) {
//        前端会返回dishId，或者setmealId，根据减少的类型
        Object dishId = map.get("dishId");
        Object setmealId = map.get("setmealId");
//        用userid和dishId/setmealId完成减少功能
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        queryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);

        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);

        Integer number = shoppingCartServiceOne.getNumber();
//        如果number = 1，那么减了就没了，直接删掉吧
        if (number == 1) {
            shoppingCartServiceOne.setNumber(0);
            shoppingCartService.removeById(shoppingCartServiceOne);
        }else{
//            number-1，然后update进去
            shoppingCartServiceOne.setNumber(number - 1);
//            update进去
            shoppingCartService.updateById(shoppingCartServiceOne);
        }
//        shoppingCartServiceOne得有数量信息，这样前端，才能实时改变数量的显示
        return R.success(shoppingCartServiceOne);

    }

    /**
     * 查看购物车，通过userid查找，然后根据创建日期排序
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车");
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
