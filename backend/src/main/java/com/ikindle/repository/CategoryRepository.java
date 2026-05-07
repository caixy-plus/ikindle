package com.ikindle.repository;

import com.ikindle.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分类Repository接口
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Repository
public interface CategoryRepository extends BaseRepository<Category, Long>, CategoryRepositoryCustom {

    /**
     * 根据编码查找分类
     */
    Category findByCode(String code);

    /**
     * 根据启用状态查找分类
     */
    List<Category> findByEnabledOrderBySortOrderAsc(Boolean enabled);

    /**
     * 查找顶级分类（没有父分类的分类）
     */
    List<Category> findByParentIsNullAndEnabledOrderBySortOrderAsc(Boolean enabled);

    /**
     * 根据父分类ID查找子分类
     */
    List<Category> findByParentIdAndEnabledOrderBySortOrderAsc(Long parentId, Boolean enabled);

    /**
     * 检查编码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 根据名称模糊查询
     */
    List<Category> findByNameContainingAndEnabled(String keyword, Boolean enabled);

    /**
     * 统计分类下的图书数量
     */
    Long countBooksByCategoryId(Long categoryId);
} 