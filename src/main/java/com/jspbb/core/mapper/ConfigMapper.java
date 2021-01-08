package com.jspbb.core.mapper;

import com.jspbb.core.domain.Config;
import org.apache.ibatis.annotations.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ConfigMapper {
    long delete(@NotNull String name);

    long insert(@NotNull Config record);

    @Nullable
    Config select(@NotNull String name);

    long update(@NotNull Config record);

    long deleteByNamePrefix(@NotNull String name);

    @NotNull
    List<Config> selectAll();
}