package com.ikindle.repository;

import com.ikindle.entity.ConfigDefinition;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigDefinitionRepository extends BaseRepository<ConfigDefinition, Long> {

    Optional<ConfigDefinition> findByConfigKey(String configKey);

    List<ConfigDefinition> findByCategoryOrderByConfigKeyAsc(String category);

    boolean existsByConfigKey(String configKey);
}
