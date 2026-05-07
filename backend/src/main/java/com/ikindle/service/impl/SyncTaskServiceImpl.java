package com.ikindle.service.impl;

import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.entity.*;
import com.ikindle.repository.*;
import com.ikindle.service.SyncTaskService;
import com.ikindle.service.UserBookshelfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class SyncTaskServiceImpl extends BaseServiceImpl<SyncTask, Long> implements SyncTaskService {

    private final SyncTaskRepository syncTaskRepository;
    private final SyncSettingRepository syncSettingRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserBookshelfService userBookshelfService;
    private final UserBookshelfRepository userBookshelfRepository;
    private final com.ikindle.service.KindleMailService kindleMailService;

    public SyncTaskServiceImpl(SyncTaskRepository syncTaskRepository,
                               SyncSettingRepository syncSettingRepository,
                               UserRepository userRepository,
                               BookRepository bookRepository,
                               OrderItemRepository orderItemRepository,
                               UserBookshelfService userBookshelfService,
                               UserBookshelfRepository userBookshelfRepository,
                               com.ikindle.service.KindleMailService kindleMailService) {
        super(syncTaskRepository);
        this.syncTaskRepository = syncTaskRepository;
        this.syncSettingRepository = syncSettingRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.orderItemRepository = orderItemRepository;
        this.userBookshelfService = userBookshelfService;
        this.userBookshelfRepository = userBookshelfRepository;
        this.kindleMailService = kindleMailService;
    }

    @Override
    public SyncTask enqueue(Long userId, Long bookId, Long orderItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
        SyncSetting setting = syncSettingRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.KINDLE_EMAIL_NOT_SET));
        if (setting.getKindleEmail() == null || setting.getKindleEmail().isBlank()) {
            throw new BusinessException(ErrorCode.KINDLE_EMAIL_NOT_SET);
        }
        SyncTask task = new SyncTask();
        task.setUser(user);
        task.setBook(book);
        if (orderItemId != null) {
            orderItemRepository.findById(orderItemId).ifPresent(task::setOrderItem);
        }
        task.setTargetEmail(setting.getKindleEmail());
        task.setStatus(SyncTask.TaskStatus.PENDING);
        return syncTaskRepository.save(task);
    }

    @Override
    public SyncTask retry(Long taskId) {
        SyncTask task = syncTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "同步任务不存在"));
        if (task.getStatus() == SyncTask.TaskStatus.SYNCING || task.getStatus() == SyncTask.TaskStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ILLEGAL, "任务无法重试");
        }
        task.setStatus(SyncTask.TaskStatus.PENDING);
        task.setErrorMsg(null);
        return syncTaskRepository.save(task);
    }

    @Override
    public SyncTask cancel(Long taskId) {
        SyncTask task = syncTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "同步任务不存在"));
        task.setStatus(SyncTask.TaskStatus.CANCELLED);
        return syncTaskRepository.save(task);
    }

    @Override
    @Async
    @Transactional
    public void executePending() {
        List<SyncTask> tasks = syncTaskRepository.findTop50ByStatusOrderByCreatedTimeAsc(SyncTask.TaskStatus.PENDING);
        for (SyncTask task : tasks) {
            try {
                process(task);
            } catch (Exception e) {
                log.error("同步任务执行异常 taskId={} err={}", task.getId(), e.getMessage(), e);
            }
        }
    }

    @Scheduled(fixedDelayString = "${ikindle.sync.poll-interval:30000}")
    public void scheduledExecute() {
        executePending();
    }

    private void process(SyncTask task) {
        task.setStatus(SyncTask.TaskStatus.SYNCING);
        syncTaskRepository.save(task);
        try {
            Book book = bookRepository.findById(task.getBook().getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
            kindleMailService.sendToKindle(task.getTargetEmail(), book);

            task.setStatus(SyncTask.TaskStatus.COMPLETED);
            task.setSyncedTime(LocalDateTime.now());
            syncTaskRepository.save(task);

            userBookshelfRepository.findByUserIdAndBookId(task.getUser().getId(), book.getId())
                    .ifPresent(bs -> userBookshelfService.updateSyncStatus(bs.getId(), UserBookshelf.SyncStatus.SYNCED));

            log.info("Kindle推送完成 taskId={} book={} target={}", task.getId(), book.getTitle(), task.getTargetEmail());
        } catch (Exception e) {
            task.setRetryCount(task.getRetryCount() + 1);
            task.setErrorMsg(e.getMessage());
            task.setStatus(task.getRetryCount() >= task.getMaxRetry()
                    ? SyncTask.TaskStatus.FAILED
                    : SyncTask.TaskStatus.PENDING);
            syncTaskRepository.save(task);
            log.warn("Kindle推送失败 taskId={} retry={}/{} err={}",
                    task.getId(), task.getRetryCount(), task.getMaxRetry(), e.getMessage());
        }
    }

    @Override
    public Page<SyncTask> listByUser(Long userId, SyncTask.TaskStatus status, Pageable pageable) {
        if (status == null) {
            return syncTaskRepository.findByUserIdOrderByCreatedTimeDesc(userId, pageable);
        }
        return syncTaskRepository.findByUserIdAndStatusOrderByCreatedTimeDesc(userId, status, pageable);
    }

    @Override
    public SyncSetting getSetting(Long userId) {
        return syncSettingRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            SyncSetting setting = new SyncSetting();
            setting.setUser(user);
            setting.setAutoSync(true);
            setting.setPriority(SyncSetting.Priority.NORMAL);
            return syncSettingRepository.save(setting);
        });
    }

    @Override
    public SyncSetting updateSetting(Long userId, String kindleEmail, Boolean autoSync, SyncSetting.Priority priority, String preferredFormat) {
        SyncSetting setting = getSetting(userId);
        if (kindleEmail != null) setting.setKindleEmail(kindleEmail);
        if (autoSync != null) setting.setAutoSync(autoSync);
        if (priority != null) setting.setPriority(priority);
        if (preferredFormat != null) setting.setPreferredFormat(preferredFormat);
        return syncSettingRepository.save(setting);
    }
}
