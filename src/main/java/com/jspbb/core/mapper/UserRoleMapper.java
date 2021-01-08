package com.jspbb.core.mapper;

import com.jspbb.core.domain.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

/**
 * 用户角色关联Mapper
 */
@Mapper
@Repository
public interface UserRoleMapper {
    long delete(UserRole key);

    long insert(UserRole record);

    long deleteByUserId(@NotNull @Param("userId") Long userId);

    long deleteByRoleId(@NotNull @Param("roleId") Long roleId);
}