package com.ikindle.repository;

import com.ikindle.entity.SyncSetting;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyncSettingRepository extends BaseRepository<SyncSetting, Long> {

    Optional<SyncSetting> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
