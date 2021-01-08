package com.jspbb.core.mapper;

import com.jspbb.core.domain.Message;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface MessageMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Message record);

    @Nullable
    Message select(@NotNull Long id);

    long update(@NotNull Message record);

    long deleteByIds(@NotNull @Param("ids") Collection<Long> ids);

    long countUnread(@NotNull @Param("userId") Long userId);

    long readByContactUserId(@NotNull @Param("userId") Long userId, @NotNull @Param("contactUserId") Long contactUserId);

    List<Message> selectByContactUserId(@NotNull @Param("userId") Long userId, @NotNull @Param("contactUserId") Long contactUserId);

    @NotNull
    List<Message> groupByUserId(Long userId);

    @NotNull
    List<Message> selectAll(@Nullable @Param("parser") QueryParser parser);

}