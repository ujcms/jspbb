package com.jspbb.core.mapper;

import com.jspbb.core.domain.Group;
import com.jspbb.core.domain.Role;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户组Mapper
 */
@Mapper
@Repository
public interface GroupMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Group record);

    @Nullable
    Group select(@NotNull Long id);

    long update(@NotNull Group record);

    long updateOrder(@NotNull @Param("id") Long id, @Param("order") int order);

    @NotNull
    List<Group> selectAll(@Nullable @Param("parser") QueryParser parser);
}