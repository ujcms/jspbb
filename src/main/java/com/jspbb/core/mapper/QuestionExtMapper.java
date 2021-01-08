package com.jspbb.core.mapper;

import com.jspbb.core.domain.QuestionExt;
import org.apache.ibatis.annotations.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * 问题扩展Mapper
 */
@Mapper
@Repository
public interface QuestionExtMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull QuestionExt record);

    @Nullable
    QuestionExt select(@NotNull Long id);

    long update(@NotNull QuestionExt record);
}