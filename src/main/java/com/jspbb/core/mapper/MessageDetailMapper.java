package com.jspbb.core.mapper;

import com.jspbb.core.domain.MessageDetail;
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
public interface MessageDetailMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull MessageDetail record);

    @Nullable
    MessageDetail select(@NotNull Long id);

    long update(@NotNull MessageDetail record);

    long minusReferredCount(@NotNull @Param("ids") Collection<Long> ids);

    long deleteOrphan(@NotNull @Param("ids") Collection<Long> ids);

    @NotNull
    List<MessageDetail> selectAll(@Nullable @Param("parser") QueryParser parser);
}