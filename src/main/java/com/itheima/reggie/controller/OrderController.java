package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }


    /**
     * 订单信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, number);

//        构造分页构造器
        Page pageInfo = new Page(page, pageSize);

//        构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
//        添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(number), Orders::getNumber, number);
//        添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

//        执行查询
        orderService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id查询订单信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Orders> getById(@PathVariable Long id){
        log.info("根据id查询订单信息...");
        Orders order = orderService.getById(id);
        if(order != null){
            return R.success(order);
        }
        return R.error("没有查询到对应订单信息");
    }

    /**
     * 根据id修改点订单信息
     * @param order
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Orders order){

//        long id = Thread.currentThread().getId();
//        log.info("update线程id为：{}",id);

        log.info(order.toString());

//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        orderService.updateById(order);

        return R.success("订单信息修改成功");
    }
}