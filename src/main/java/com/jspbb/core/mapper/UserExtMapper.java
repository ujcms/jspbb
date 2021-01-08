package com.jspbb.core.mapper;

import com.jspbb.core.domain.UserExt;
import org.apache.ibatis.annotations.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserExtMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull UserExt record);

    @Nullable
    UserExt select(@NotNull Long id);

    long update(@NotNull UserExt record);
}