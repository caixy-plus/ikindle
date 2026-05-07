package com.ikindle.service;

import com.ikindle.entity.UserBookshelf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户书架 Service
 */
public interface UserBookshelfService extends BaseService<UserBookshelf, Long> {

    UserBookshelf addOrUpdate(Long userId, Long bookId);

    UserBookshelf updateProgress(Long bookshelfId, Integer progress, Double percentage);

    UserBookshelf toggleFavorite(Long bookshelfId);

    UserBookshelf updateSyncStatus(Long bookshelfId, UserBookshelf.SyncStatus status);

    Page<UserBookshelf> listByUser(Long userId, UserBookshelf.SyncStatus syncStatus, Boolean favoriteOnly, Pageable pageable);

    void removeByUserAndBook(Long userId, Long bookId);
}
