package com.jspbb.core.mapper;

import com.jspbb.core.domain.UserRestrict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户限制Mapper
 */
@Mapper
@Repository
public interface UserRestrictMapper {
    long delete(@NotNull Long userId, @NotNull String name);

    long insert(@NotNull UserRestrict record);

    @Nullable
    UserRestrict select(@NotNull @Param("userId") Long userId, @NotNull  @Param("name")String name);

    long update(@NotNull UserRestrict record);

    @NotNull
    List<UserRestrict> selectByUserId(@NotNull Long userId);

    long deleteByUserId(@NotNull Long userId);
}