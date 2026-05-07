package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.BookDTO;
import com.ikindle.dto.PageResponse;
import com.ikindle.entity.Book;
import com.ikindle.mapper.BookDtoMapper;
import com.ikindle.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookDtoMapper bookDtoMapper;

    @GetMapping
    public ApiResponse<PageResponse<BookDTO>> listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "createdTime,desc") String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Book> result;
        if (keyword != null && !keyword.isBlank()) {
            result = bookService.searchBooks(keyword, pageable);
        } else if (tagId != null) {
            result = bookService.findByTagId(tagId, pageable);
        } else if (minPrice != null || maxPrice != null) {
            result = bookService.findByPriceRange(
                    minPrice != null ? minPrice : BigDecimal.ZERO,
                    maxPrice != null ? maxPrice : new BigDecimal("999999"),
                    pageable);
        } else if (categoryId != null) {
            result = bookService.findByCategoryIdAndPublished(categoryId, true, pageable);
        } else {
            result = bookService.findByPublished(true, pageable);
        }
        return ApiResponse.success(toPage(result));
    }

    @GetMapping("/hot")
    public ApiResponse<PageResponse<BookDTO>> hotBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(toPage(bookService.findHotBooks(PageRequest.of(page, size))));
    }

    @GetMapping("/recommended")
    public ApiResponse<PageResponse<BookDTO>> recommendedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(toPage(bookService.findRecommendedBooks(PageRequest.of(page, size))));
    }

    @GetMapping("/latest")
    public ApiResponse<PageResponse<BookDTO>> latestBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(toPage(bookService.findLatestBooks(PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    public ApiResponse<BookDTO> getBookById(@PathVariable Long id) {
        return ApiResponse.success(bookDtoMapper.toDto(bookService.findByIdOrThrow(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        Book saved = bookService.save(bookDtoMapper.toEntity(bookDTO));
        return ApiResponse.success(bookDtoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        bookDTO.setId(id);
        Book book = bookDtoMapper.toEntity(bookDTO);
        book.setId(id);
        Book updated = bookService.update(id, book);
        return ApiResponse.success(bookDtoMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return ApiResponse.success();
    }

    private Sort parseSort(String spec) {
        String[] parts = spec.split(",");
        Sort.Direction dir = parts.length > 1 && "asc".equalsIgnoreCase(parts[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(dir, parts[0]);
    }

    private PageResponse<BookDTO> toPage(Page<Book> page) {
        List<BookDTO> items = page.getContent().stream()
                .map(bookDtoMapper::toDto)
                .collect(Collectors.toList());
        return PageResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
