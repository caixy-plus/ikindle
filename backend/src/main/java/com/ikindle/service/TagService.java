package com.ikindle.service;

import com.ikindle.entity.Tag;

import java.util.List;

/**
 * 标签 Service
 */
public interface TagService extends BaseService<Tag, Long> {

    Tag findByCode(String code);

    List<Tag> findEnabled();

    /**
     * 热门标签
     */
    List<Tag> findPopularTags();

    boolean existsByCode(String code);

    List<Tag> searchByName(String keyword);

    void incrementUsage(Long tagId);
}
