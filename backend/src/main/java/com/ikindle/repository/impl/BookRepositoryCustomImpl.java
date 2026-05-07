package com.ikindle.repository.impl;

import com.ikindle.entity.Book;
import com.ikindle.entity.QBook;
import com.ikindle.entity.QTag;
import com.ikindle.repository.BookRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 图书复杂查询实现 (QueryDSL)
 */
@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public BookRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Book> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        QBook book = QBook.book;
        BooleanBuilder where = new BooleanBuilder();
        where.and(book.published.isTrue());
        where.and(book.isDeleted.isFalse());
        if (minPrice != null) where.and(book.price.goe(minPrice));
        if (maxPrice != null) where.and(book.price.loe(maxPrice));

        List<Book> content = queryFactory.selectFrom(book)
                .where(where)
                .orderBy(book.salesCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory.select(book.count()).from(book).where(where).fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        QBook book = QBook.book;
        BooleanBuilder where = new BooleanBuilder();
        where.and(book.published.isTrue());
        where.and(book.isDeleted.isFalse());
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            where.and(book.title.like(like)
                    .or(book.author.like(like))
                    .or(book.description.like(like))
                    .or(book.subtitle.like(like)));
        }

        List<Book> content = queryFactory.selectFrom(book)
                .where(where)
                .orderBy(book.salesCount.desc(), book.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory.select(book.count()).from(book).where(where).fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Book> findByTagId(Long tagId, Pageable pageable) {
        QBook book = QBook.book;
        QTag tag = QTag.tag;
        BooleanBuilder where = new BooleanBuilder();
        where.and(book.published.isTrue());
        where.and(book.isDeleted.isFalse());
        where.and(tag.id.eq(tagId));

        List<Book> content = queryFactory.selectFrom(book)
                .leftJoin(book.tags, tag)
                .where(where)
                .orderBy(book.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();
        Long total = queryFactory.select(book.countDistinct())
                .from(book)
                .leftJoin(book.tags, tag)
                .where(where)
                .fetchOne();
        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    @Override
    public Page<Book> findHotBooks(Pageable pageable) {
        QBook book = QBook.book;
        BooleanBuilder where = new BooleanBuilder();
        where.and(book.published.isTrue());
        where.and(book.isDeleted.isFalse());

        List<Book> content = queryFactory.selectFrom(book)
                .where(where)
                .orderBy(book.salesCount.desc(), book.rating.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory.select(book.count()).from(book).where(where).fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Book> findRecommendedBooks(Pageable pageable) {
        QBook book = QBook.book;
        BooleanBuilder where = new BooleanBuilder();
        where.and(book.published.isTrue());
        where.and(book.isDeleted.isFalse());
        where.and(book.rating.goe(4.0));

        List<Book> content = queryFactory.selectFrom(book)
                .where(where)
                .orderBy(book.rating.desc(), book.salesCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory.select(book.count()).from(book).where(where).fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Book> findLatestBooks(Pageable pageable) {
        QBook book = QBook.book;
        BooleanBuilder where = new BooleanBuilder();
        where.and(book.published.isTrue());
        where.and(book.isDeleted.isFalse());

        List<Book> content = queryFactory.selectFrom(book)
                .where(where)
                .orderBy(book.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory.select(book.count()).from(book).where(where).fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Long countByCategoryId(Long categoryId) {
        QBook book = QBook.book;
        return queryFactory.select(book.count())
                .from(book)
                .where(book.category.id.eq(categoryId)
                        .and(book.published.isTrue())
                        .and(book.isDeleted.isFalse()))
                .fetchOne();
    }
}
