package com.ikindle.repository;

import com.ikindle.entity.UserBookshelf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户书架 Repository
 */
@Repository
public interface UserBookshelfRepository extends BaseRepository<UserBookshelf, Long> {

    Page<UserBookshelf> findByUserIdOrderByLastReadTimeDesc(Long userId, Pageable pageable);

    Page<UserBookshelf> findByUserIdAndSyncStatusOrderByCreatedTimeDesc(Long userId,
                                                                       UserBookshelf.SyncStatus syncStatus,
                                                                       Pageable pageable);

    Page<UserBookshelf> findByUserIdAndIsFavoriteTrueOrderByFavoriteTimeDesc(Long userId, Pageable pageable);

    Optional<UserBookshelf> findByUserIdAndBookId(Long userId, Long bookId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    long countByUserId(Long userId);
}
