package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 根据id关联删除
     * @param id
     */
    public void remove(Long id);

}