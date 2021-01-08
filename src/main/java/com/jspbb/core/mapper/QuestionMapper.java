package com.jspbb.core.mapper;

import com.jspbb.core.domain.Question;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 问题Mapper
 */
@Mapper
@Repository
public interface QuestionMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Question record);

    @Nullable
    Question select(@NotNull Long id);

    long update(@NotNull Question record);

    @NotNull
    List<Question> selectAll(@Nullable @Param("parser") QueryParser parser);
}