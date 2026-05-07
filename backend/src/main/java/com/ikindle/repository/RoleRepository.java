package com.ikindle.repository;

import com.ikindle.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 角色Repository接口
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Repository
public interface RoleRepository extends BaseRepository<Role, Long> {

    /**
     * 根据名称查找角色
     */
    Optional<Role> findByName(String name);

    /**
     * 检查角色名称是否存在
     */
    boolean existsByName(String name);
} 