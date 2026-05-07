package com.ikindle.service.impl;

import com.ikindle.entity.Tag;
import com.ikindle.repository.TagRepository;
import com.ikindle.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagServiceImpl extends BaseServiceImpl<Tag, Long> implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        super(tagRepository);
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag findByCode(String code) {
        return tagRepository.findByCode(code);
    }

    @Override
    public List<Tag> findEnabled() {
        return tagRepository.findByEnabledOrderByUsageCountDesc(true);
    }

    @Override
    public List<Tag> findPopularTags() {
        return tagRepository.findPopularTags();
    }

    @Override
    public boolean existsByCode(String code) {
        return tagRepository.existsByCode(code);
    }

    @Override
    public List<Tag> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return tagRepository.findByEnabledOrderByUsageCountDesc(true);
        }
        return tagRepository.findByNameContainingAndEnabled(keyword, true);
    }

    @Override
    public void incrementUsage(Long tagId) {
        tagRepository.findById(tagId).ifPresent(tag -> {
            tag.setUsageCount((tag.getUsageCount() == null ? 0 : tag.getUsageCount()) + 1);
            tagRepository.save(tag);
        });
    }
}
