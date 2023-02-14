package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 保存套餐信息
     * 涉及两个表setmeal 以及 setmeal_dish
     * setmeal存储基本信息，包括自身id，所属套餐categoryId，等
     * setmeal_dish存储套餐以及套餐包含菜品信息，包括套餐id，以及菜品dishId
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value="setmeal", allEntries=true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息 {}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("套餐分页查询,{},{},{}",page, pageSize, name);
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

//        构造查询条件
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        套餐名模糊查询
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
//        更新日期逆排序
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
//        分页查询,查询后pageInfo中会有数据
        setmealService.page(pageInfo, lambdaQueryWrapper);

//        对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "setmealDishes");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {

            SetmealDto setmealDto = new SetmealDto();
            //            拷贝
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
//            查套餐分类名称
            Category category = categoryService.getById(categoryId);
//            给setmealDto设置名称
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

//        将List<SetmealDto> list设置给setmealDtoPage的records
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);

    }

    /**
     * 处理删除请求
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value="setmeal", allEntries=true)
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("批量删除ids : {}", ids);
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    /**
     * 根据CategoryId，找对应的套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value="setmeal", key="#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        根据菜的种类返回菜
        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
//        找在售的
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);

        List<Setmeal> setmeals = setmealService.list(lambdaQueryWrapper);

        return R.success(setmeals);
    }
}
