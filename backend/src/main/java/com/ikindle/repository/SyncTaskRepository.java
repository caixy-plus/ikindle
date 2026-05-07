package com.ikindle.repository;

import com.ikindle.entity.SyncTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncTaskRepository extends BaseRepository<SyncTask, Long> {

    Page<SyncTask> findByUserIdOrderByCreatedTimeDesc(Long userId, Pageable pageable);

    Page<SyncTask> findByUserIdAndStatusOrderByCreatedTimeDesc(Long userId, SyncTask.TaskStatus status, Pageable pageable);

    List<SyncTask> findTop50ByStatusOrderByCreatedTimeAsc(SyncTask.TaskStatus status);
}
