package com.ikindle.service.impl;

import com.ikindle.entity.Book;
import com.ikindle.repository.BookRepository;
import com.ikindle.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 图书Service实现类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Service
@Transactional
public class BookServiceImpl extends BaseServiceImpl<Book, Long> implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        super(bookRepository);
        this.bookRepository = bookRepository;
    }

    @Override
    public Page<Book> findByPublished(Boolean published, Pageable pageable) {
        return bookRepository.findByPublished(published, pageable);
    }

    @Override
    public Page<Book> findByCategoryIdAndPublished(Long categoryId, Boolean published, Pageable pageable) {
        return bookRepository.findByCategoryIdAndPublished(categoryId, published, pageable);
    }

    @Override
    public Page<Book> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return bookRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchBooks(keyword, pageable);
    }

    @Override
    public Page<Book> findByTagId(Long tagId, Pageable pageable) {
        return bookRepository.findByTagId(tagId, pageable);
    }

    @Override
    public Page<Book> findHotBooks(Pageable pageable) {
        return bookRepository.findHotBooks(pageable);
    }

    @Override
    public Page<Book> findRecommendedBooks(Pageable pageable) {
        return bookRepository.findRecommendedBooks(pageable);
    }

    @Override
    public Page<Book> findLatestBooks(Pageable pageable) {
        return bookRepository.findLatestBooks(pageable);
    }

    @Override
    public List<Book> findByFileFormatAndPublished(String fileFormat, Boolean published) {
        return bookRepository.findByFileFormatAndPublished(fileFormat, published);
    }

    @Override
    public Long countByCategoryId(Long categoryId) {
        return bookRepository.countByCategoryId(categoryId);
    }

    @Override
    public void updateSalesCount(Long bookId, Integer quantity) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setSalesCount(book.getSalesCount() + quantity);
            bookRepository.save(book);
        }
    }

    @Override
    public void updateRating(Long bookId, Double rating) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            // 这里可以实现更复杂的评级计算逻辑
            book.setRating(rating);
            bookRepository.save(book);
        }
    }

    @Override
    public boolean existsByTitleAndAuthor(String title, String author) {
        return bookRepository.existsByTitleAndAuthor(title, author);
    }

    @Override
    public Optional<Book> findByIdWithDetails(Long id) {
        return bookRepository.findById(id);
    }
} 