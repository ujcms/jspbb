package com.jspbb.core.mapper;

import com.jspbb.core.domain.Notification;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NotificationMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Notification record);

    @Nullable
    Notification select(@NotNull Long id);

    long update(@NotNull Notification record);

    long deleteByTypeAndData(@Nullable @Param("userId") Long userId, @Nullable @Param("type") String type, @Nullable @Param("data") String data);

    long countByUserId(@NotNull @Param("userId") Long userId);

    @NotNull
    List<Notification> selectAll(@Nullable @Param("parser") QueryParser parser);

}