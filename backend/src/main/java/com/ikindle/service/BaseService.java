package com.ikindle.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 基础Service接口
 * 提供通用的业务逻辑方法
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
public interface BaseService<T, ID> {

    /**
     * 保存实体
     */
    T save(T entity);

    /**
     * 根据ID更新实体
     */
    T update(ID id, T entity);

    /**
     * 批量保存实体
     */
    List<T> saveAll(List<T> entities);

    /**
     * 根据ID查找实体
     */
    Optional<T> findById(ID id);

    /**
     * 根据ID查找实体，如果不存在则抛出异常
     */
    T findByIdOrThrow(ID id);

    /**
     * 查找所有实体
     */
    List<T> findAll();

    /**
     * 分页查找实体
     */
    Page<T> findAll(Pageable pageable);

    /**
     * 根据ID删除实体
     */
    void deleteById(ID id);

    /**
     * 删除实体
     */
    void delete(T entity);

    /**
     * 批量删除实体
     */
    void deleteAll(List<T> entities);

    /**
     * 检查实体是否存在
     */
    boolean existsById(ID id);

    /**
     * 统计实体数量
     */
    long count();
} 