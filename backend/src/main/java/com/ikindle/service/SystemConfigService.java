package com.ikindle.service;

import com.ikindle.entity.ConfigDefinition;
import com.ikindle.entity.SystemConfig;

import java.util.List;

public interface SystemConfigService extends BaseService<SystemConfig, Long> {

    /**
     * 读取配置值(优先 SystemConfig.config_value,否则 ConfigDefinition.default_value)
     */
    String get(String configKey);

    String getOrDefault(String configKey, String defaultValue);

    Boolean getBoolean(String configKey);

    Integer getInt(String configKey);

    /**
     * 设置配置值,同步刷新缓存
     */
    SystemConfig set(String configKey, String configValue);

    /**
     * 注册配置定义
     */
    ConfigDefinition registerDefinition(ConfigDefinition definition);

    List<ConfigDefinition> listDefinitions(String category);
}
