package com.ikindle.service;

import com.ikindle.entity.SyncSetting;
import com.ikindle.entity.SyncTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SyncTaskService extends BaseService<SyncTask, Long> {

    SyncTask enqueue(Long userId, Long bookId, Long orderItemId);

    SyncTask retry(Long taskId);

    SyncTask cancel(Long taskId);

    void executePending();

    Page<SyncTask> listByUser(Long userId, SyncTask.TaskStatus status, Pageable pageable);

    SyncSetting getSetting(Long userId);

    SyncSetting updateSetting(Long userId, String kindleEmail, Boolean autoSync, SyncSetting.Priority priority, String preferredFormat);
}
