package com.ikindle.repository;

import com.ikindle.entity.Dict;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 字典Repository
 */
@Repository
public interface DictRepository extends BaseRepository<Dict, Long> {

    List<Dict> findByType(String type);

    Optional<Dict> findByTypeAndValue(String type, String value);

    Optional<Dict> findByTypeAndLabel(String type, String label);

    List<Dict> findByTypeAndEnabled(String type, Boolean enabled);

    List<Dict> findByTypeAndEnabledOrderBySortAsc(String type, Boolean enabled);
}
