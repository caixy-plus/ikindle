package com.ikindle.service.impl;

import com.ikindle.entity.ConfigDefinition;
import com.ikindle.entity.SystemConfig;
import com.ikindle.repository.ConfigDefinitionRepository;
import com.ikindle.repository.SystemConfigRepository;
import com.ikindle.service.SystemConfigService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class SystemConfigServiceImpl extends BaseServiceImpl<SystemConfig, Long> implements SystemConfigService {

    private static final String CACHE_PREFIX = "ikindle:config:";
    private static final Duration TTL = Duration.ofMinutes(30);

    private final SystemConfigRepository configRepository;
    private final ConfigDefinitionRepository definitionRepository;
    private final StringRedisTemplate redisTemplate;

    public SystemConfigServiceImpl(SystemConfigRepository configRepository,
                                   ConfigDefinitionRepository definitionRepository,
                                   StringRedisTemplate redisTemplate) {
        super(configRepository);
        this.configRepository = configRepository;
        this.definitionRepository = definitionRepository;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void warmUpCache() {
        try {
            configRepository.findAll().forEach(c ->
                    redisTemplate.opsForValue().set(CACHE_PREFIX + c.getConfigKey(), c.getConfigValue(), TTL));
        } catch (Exception e) {
            log.warn("系统参数缓存预热失败: {}", e.getMessage());
        }
    }

    @Override
    public String get(String configKey) {
        String cached = safeGetFromRedis(configKey);
        if (cached != null) return cached;

        Optional<SystemConfig> config = configRepository.findByConfigKey(configKey);
        if (config.isPresent()) {
            safeSetCache(configKey, config.get().getConfigValue());
            return config.get().getConfigValue();
        }
        return definitionRepository.findByConfigKey(configKey)
                .map(ConfigDefinition::getDefaultValue)
                .orElse(null);
    }

    @Override
    public String getOrDefault(String configKey, String defaultValue) {
        String value = get(configKey);
        return value != null ? value : defaultValue;
    }

    @Override
    public Boolean getBoolean(String configKey) {
        String value = get(configKey);
        return value != null && Boolean.parseBoolean(value);
    }

    @Override
    public Integer getInt(String configKey) {
        String value = get(configKey);
        try {
            return value == null ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public SystemConfig set(String configKey, String configValue) {
        SystemConfig config = configRepository.findByConfigKey(configKey).orElseGet(() -> {
            SystemConfig c = new SystemConfig();
            c.setConfigKey(configKey);
            return c;
        });
        config.setConfigValue(configValue);
        SystemConfig saved = configRepository.save(config);
        safeSetCache(configKey, configValue);
        return saved;
    }

    @Override
    public ConfigDefinition registerDefinition(ConfigDefinition definition) {
        return definitionRepository.findByConfigKey(definition.getConfigKey())
                .orElseGet(() -> definitionRepository.save(definition));
    }

    @Override
    public List<ConfigDefinition> listDefinitions(String category) {
        if (category == null || category.isBlank()) {
            return definitionRepository.findAll();
        }
        return definitionRepository.findByCategoryOrderByConfigKeyAsc(category);
    }

    private String safeGetFromRedis(String key) {
        try {
            return redisTemplate.opsForValue().get(CACHE_PREFIX + key);
        } catch (Exception e) {
            return null;
        }
    }

    private void safeSetCache(String key, String value) {
        if (value == null) return;
        try {
            redisTemplate.opsForValue().set(CACHE_PREFIX + key, value, TTL);
        } catch (Exception ignored) {
        }
    }
}
