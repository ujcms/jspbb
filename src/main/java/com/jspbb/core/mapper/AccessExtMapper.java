package com.jspbb.core.mapper;

import com.jspbb.core.domain.AccessExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * 访问扩展Mapper
 */
@Mapper
@Repository
public interface AccessExtMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull AccessExt record);

    @Nullable
    AccessExt select(@NotNull Long id);

    long update(@NotNull AccessExt record);

}