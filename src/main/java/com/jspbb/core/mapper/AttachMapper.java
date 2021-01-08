package com.jspbb.core.mapper;

import com.jspbb.core.domain.Attach;
import org.apache.ibatis.annotations.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * 附件Mapper
 */
@Mapper
@Repository
public interface AttachMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Attach record);

    @Nullable
    Attach select(@NotNull Long id);

    long update(@NotNull Attach record);

    @Nullable
    Attach selectByName(@NotNull String name);

    @Nullable
    Attach selectByUrl(@NotNull String url);
}