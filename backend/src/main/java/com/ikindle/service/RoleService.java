package com.ikindle.service;

import com.ikindle.entity.Role;

import java.util.List;

public interface RoleService extends BaseService<Role, Long> {

    Role findByName(String name);

    Role assignPermissions(Long roleId, List<Long> permissionIds);
}
