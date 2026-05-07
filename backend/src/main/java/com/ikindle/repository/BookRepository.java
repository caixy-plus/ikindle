package com.ikindle.repository;

import com.ikindle.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 图书Repository接口
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Repository
public interface BookRepository extends BaseRepository<Book, Long>, BookRepositoryCustom {

    /**
     * 根据上架状态查找图书
     */
    Page<Book> findByPublished(Boolean published, Pageable pageable);

    /**
     * 根据分类查找图书
     */
    Page<Book> findByCategoryIdAndPublished(Long categoryId, Boolean published, Pageable pageable);

    /**
     * 根据文件格式查找图书
     */
    List<Book> findByFileFormatAndPublished(String fileFormat, Boolean published);

    /**
     * 是否存在标题+作者组合
     */
    boolean existsByTitleAndAuthor(String title, String author);

    /**
     * 统计分类下的图书数量
     */
    @Override
    Long countByCategoryId(Long categoryId);
} 