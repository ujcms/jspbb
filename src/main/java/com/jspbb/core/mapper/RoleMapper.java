package com.jspbb.core.mapper;

import com.jspbb.core.domain.Role;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色Mapper
 */
@Mapper
@Repository
public interface RoleMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Role record);

    @Nullable
    Role select(@NotNull Long id);

    long update(@NotNull Role record);

    long updateOrder(@NotNull @Param("id") Long id, @Param("order") int order);

    @NotNull
    List<Role> selectAll(@Nullable @Param("parser") QueryParser parser);

    @NotNull
    List<Role> selectByUserId(@NotNull Long userId);
}