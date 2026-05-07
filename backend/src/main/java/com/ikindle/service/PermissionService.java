package com.ikindle.service;

import com.ikindle.entity.Permission;

public interface PermissionService extends BaseService<Permission, Long> {

    Permission findByName(String name);
}
