package com.ikindle.service.impl;

import com.ikindle.entity.Category;
import com.ikindle.repository.CategoryRepository;
import com.ikindle.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类Service实现类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Service
@Transactional
public class CategoryServiceImpl extends BaseServiceImpl<Category, Long> implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category findByCode(String code) {
        return categoryRepository.findByCode(code);
    }

    @Override
    public List<Category> findByEnabledOrderBySortOrderAsc(Boolean enabled) {
        return categoryRepository.findByEnabledOrderBySortOrderAsc(enabled);
    }

    @Override
    public List<Category> findTopLevelCategories() {
        return categoryRepository.findTopLevelCategories();
    }

    @Override
    public List<Category> findByParentIdAndEnabledOrderBySortOrderAsc(Long parentId, Boolean enabled) {
        return categoryRepository.findByParentIdAndEnabledOrderBySortOrderAsc(parentId, enabled);
    }

    @Override
    public boolean existsByCode(String code) {
        return categoryRepository.existsByCode(code);
    }

    @Override
    public List<Category> findByNameContaining(String keyword) {
        return categoryRepository.findByNameContaining(keyword);
    }

    @Override
    public Long countBooksByCategoryId(Long categoryId) {
        return categoryRepository.countBooksByCategoryId(categoryId);
    }
} 