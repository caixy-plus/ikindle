package com.ikindle.repository;

import com.ikindle.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 标签Repository
 */
@Repository
public interface TagRepository extends BaseRepository<Tag, Long> {

    Tag findByCode(String code);

    List<Tag> findByEnabledOrderByUsageCountDesc(Boolean enabled);

    /**
     * 查找热门标签（按使用次数排序,只返回 enabled = true 的前 50 条）
     */
    @Query("SELECT t FROM Tag t WHERE t.enabled = true AND t.isDeleted = false ORDER BY t.usageCount DESC")
    List<Tag> findPopularTags();

    boolean existsByCode(String code);

    List<Tag> findByNameContainingAndEnabled(String keyword, Boolean enabled);

    List<Tag> findByColorAndEnabled(String color, Boolean enabled);

    List<Tag> findByUsageCountGreaterThanAndEnabled(Integer minUsageCount, Boolean enabled);
}
