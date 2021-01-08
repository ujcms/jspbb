package com.jspbb.core.mapper;

import com.jspbb.core.domain.Comment;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论Mapper
 */
@Mapper
@Repository
public interface CommentMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Comment record);

    @Nullable
    Comment select(@NotNull Long id);

    long update(@NotNull Comment record);

    @NotNull
    List<Comment> selectAll(@Nullable @Param("parser") QueryParser parser);

    long updateParentId(@NotNull @Param("origParentId") Long origParentId, @Param("parentId") Long parentId);
}