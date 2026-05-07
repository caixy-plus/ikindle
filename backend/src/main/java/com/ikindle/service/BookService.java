package com.ikindle.service;

import com.ikindle.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 图书Service接口
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
public interface BookService extends BaseService<Book, Long> {

    /**
     * 根据上架状态分页查找图书
     */
    Page<Book> findByPublished(Boolean published, Pageable pageable);

    /**
     * 根据分类查找图书
     */
    Page<Book> findByCategoryIdAndPublished(Long categoryId, Boolean published, Pageable pageable);

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
     * 根据文件格式查找图书
     */
    List<Book> findByFileFormatAndPublished(String fileFormat, Boolean published);

    /**
     * 统计分类下的图书数量
     */
    Long countByCategoryId(Long categoryId);

    /**
     * 更新图书销量
     */
    void updateSalesCount(Long bookId, Integer quantity);

    /**
     * 更新图书评级
     */
    void updateRating(Long bookId, Double rating);

    /**
     * 检查图书是否存在
     */
    boolean existsByTitleAndAuthor(String title, String author);

    /**
     * 根据ID查找图书（包含分类和标签信息）
     */
    Optional<Book> findByIdWithDetails(Long id);
} 