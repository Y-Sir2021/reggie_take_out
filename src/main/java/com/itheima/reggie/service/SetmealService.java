package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 保存套餐信息
     *  涉及两个表setmeal 以及 setmeal_dish
     *  setmeal存储基本信息，包括自身id，所属套餐categoryId，等
     *  setmeal_dish存储套餐以及套餐包含菜品信息，包括套餐id，以及菜品dishId
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);



    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
