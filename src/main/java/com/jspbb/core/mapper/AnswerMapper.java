package com.jspbb.core.mapper;

import com.jspbb.core.domain.Answer;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 回答Mapper
 */
@Mapper
@Repository
public interface AnswerMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Answer record);

    @Nullable
    Answer select(@NotNull Long id);

    long update(@NotNull Answer record);

    @NotNull
    List<Answer> selectList(@Nullable @Param("questionId") Long questionId);

    @NotNull
    List<Answer> selectAll(@Nullable @Param("parser") QueryParser parser);
}