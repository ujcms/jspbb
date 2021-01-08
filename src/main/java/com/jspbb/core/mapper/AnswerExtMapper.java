package com.jspbb.core.mapper;

import com.jspbb.core.domain.AnswerExt;
import org.apache.ibatis.annotations.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * 回答扩展Mapper
 */
@Mapper
@Repository
public interface AnswerExtMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull AnswerExt record);

    @Nullable
    AnswerExt select(@NotNull Long id);

    long update(@NotNull AnswerExt record);
}