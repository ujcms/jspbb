package com.jspbb.core.mapper;

import com.jspbb.core.domain.Seq;
import org.apache.ibatis.annotations.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SeqMapper {
    long delete(@NotNull String name);

    long insert(@NotNull Seq record);

    @Nullable
    Seq select(@NotNull String name);

    long update(@NotNull Seq record);

    @Nullable
    Seq selectForUpdate(@NotNull String name);
}