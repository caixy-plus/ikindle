package com.ikindle.repository;

import com.ikindle.entity.Permission;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 权限Repository接口
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {

    /**
     * 根据名称查找权限
     */
    Optional<Permission> findByName(String name);

    /**
     * 检查权限名称是否存在
     */
    boolean existsByName(String name);
} 