package com.ikindle.repository;

import com.ikindle.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * 图书Repository自定义接口
 * 使用QueryDSL实现复杂查询
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
public interface BookRepositoryCustom {

    /**
     * 根据价格范围查找图书
     */
    Page<Book> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * 根据关键词搜索图书
     */
    Page<Book> searchBooks(String keyword, Pageable pageable);

    /**
     * 根据标签查找图书
     */
    Page<Book> findByTagId(Long tagId, Pageable pageable);

    /**
     * 查找热门图书
     */
    Page<Book> findHotBooks(Pageable pageable);

    /**
     * 查找推荐图书
     */
    Page<Book> findRecommendedBooks(Pageable pageable);

    /**
     * 查找最新图书
     */
    Page<Book> findLatestBooks(Pageable pageable);

    /**
     * 统计分类下的图书数量
     */
    Long countByCategoryId(Long categoryId);
} 