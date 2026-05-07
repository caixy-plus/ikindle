package com.ikindle.repository;

import com.ikindle.entity.Category;

import java.util.List;

/**
 * 分类Repository自定义接口
 * 使用QueryDSL实现复杂查询
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
public interface CategoryRepositoryCustom {

    /**
     * 查找顶级分类
     */
    List<Category> findTopLevelCategories();

    /**
     * 根据名称模糊查询
     */
    List<Category> findByNameContaining(String keyword);

    /**
     * 统计分类下的图书数量
     */
    Long countBooksByCategoryId(Long categoryId);
} 