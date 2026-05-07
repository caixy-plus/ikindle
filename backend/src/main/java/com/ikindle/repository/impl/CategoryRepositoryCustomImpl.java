package com.ikindle.repository.impl;

import com.ikindle.entity.Category;
import com.ikindle.entity.QBook;
import com.ikindle.entity.QCategory;
import com.ikindle.repository.CategoryRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分类复杂查询实现 (QueryDSL)
 */
@Repository
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public CategoryRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Category> findTopLevelCategories() {
        QCategory category = QCategory.category;
        return queryFactory.selectFrom(category)
                .where(category.parent.isNull()
                        .and(category.enabled.isTrue())
                        .and(category.isDeleted.isFalse()))
                .orderBy(category.sortOrder.asc())
                .fetch();
    }

    @Override
    public List<Category> findByNameContaining(String keyword) {
        QCategory category = QCategory.category;
        if (keyword == null || keyword.isBlank()) {
            return queryFactory.selectFrom(category)
                    .where(category.enabled.isTrue().and(category.isDeleted.isFalse()))
                    .orderBy(category.sortOrder.asc())
                    .fetch();
        }
        return queryFactory.selectFrom(category)
                .where(category.name.like("%" + keyword + "%")
                        .and(category.enabled.isTrue())
                        .and(category.isDeleted.isFalse()))
                .orderBy(category.sortOrder.asc())
                .fetch();
    }

    @Override
    public Long countBooksByCategoryId(Long categoryId) {
        QBook book = QBook.book;
        return queryFactory.select(book.count())
                .from(book)
                .where(book.category.id.eq(categoryId)
                        .and(book.published.isTrue())
                        .and(book.isDeleted.isFalse()))
                .fetchOne();
    }
}
