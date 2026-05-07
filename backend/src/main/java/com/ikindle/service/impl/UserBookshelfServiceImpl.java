package com.ikindle.service.impl;

import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.entity.Book;
import com.ikindle.entity.User;
import com.ikindle.entity.UserBookshelf;
import com.ikindle.repository.BookRepository;
import com.ikindle.repository.UserBookshelfRepository;
import com.ikindle.repository.UserRepository;
import com.ikindle.service.UserBookshelfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class UserBookshelfServiceImpl extends BaseServiceImpl<UserBookshelf, Long> implements UserBookshelfService {

    private final UserBookshelfRepository bookshelfRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserBookshelfServiceImpl(UserBookshelfRepository bookshelfRepository,
                                    UserRepository userRepository,
                                    BookRepository bookRepository) {
        super(bookshelfRepository);
        this.bookshelfRepository = bookshelfRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public UserBookshelf addOrUpdate(Long userId, Long bookId) {
        return bookshelfRepository.findByUserIdAndBookId(userId, bookId)
                .map(existing -> {
                    if (existing.getSyncStatus() == UserBookshelf.SyncStatus.COMPLETED
                            || existing.getSyncStatus() == UserBookshelf.SyncStatus.SYNCED) {
                        return existing;
                    }
                    existing.setSyncStatus(UserBookshelf.SyncStatus.PENDING);
                    return bookshelfRepository.save(existing);
                })
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                    Book book = bookRepository.findById(bookId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
                    UserBookshelf bs = new UserBookshelf();
                    bs.setUser(user);
                    bs.setBook(book);
                    bs.setSyncStatus(UserBookshelf.SyncStatus.PENDING);
                    bs.setReadingProgress(0);
                    bs.setReadingPercentage(0.0);
                    bs.setIsFavorite(false);
                    bs.setReadingDuration(0L);
                    return bookshelfRepository.save(bs);
                });
    }

    @Override
    public UserBookshelf updateProgress(Long bookshelfId, Integer progress, Double percentage) {
        UserBookshelf bs = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "书架记录不存在"));
        bs.setReadingProgress(progress);
        bs.setReadingPercentage(percentage);
        bs.setLastReadTime(LocalDateTime.now());
        return bookshelfRepository.save(bs);
    }

    @Override
    public UserBookshelf toggleFavorite(Long bookshelfId) {
        UserBookshelf bs = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "书架记录不存在"));
        boolean newState = !Boolean.TRUE.equals(bs.getIsFavorite());
        bs.setIsFavorite(newState);
        bs.setFavoriteTime(newState ? LocalDateTime.now() : null);
        return bookshelfRepository.save(bs);
    }

    @Override
    public UserBookshelf updateSyncStatus(Long bookshelfId, UserBookshelf.SyncStatus status) {
        UserBookshelf bs = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "书架记录不存在"));
        bs.setSyncStatus(status);
        return bookshelfRepository.save(bs);
    }

    @Override
    public Page<UserBookshelf> listByUser(Long userId, UserBookshelf.SyncStatus syncStatus, Boolean favoriteOnly, Pageable pageable) {
        if (Boolean.TRUE.equals(favoriteOnly)) {
            return bookshelfRepository.findByUserIdAndIsFavoriteTrueOrderByFavoriteTimeDesc(userId, pageable);
        }
        if (syncStatus != null) {
            return bookshelfRepository.findByUserIdAndSyncStatusOrderByCreatedTimeDesc(userId, syncStatus, pageable);
        }
        return bookshelfRepository.findByUserIdOrderByLastReadTimeDesc(userId, pageable);
    }

    @Override
    public void removeByUserAndBook(Long userId, Long bookId) {
        bookshelfRepository.findByUserIdAndBookId(userId, bookId)
                .ifPresent(bookshelfRepository::delete);
    }
}
