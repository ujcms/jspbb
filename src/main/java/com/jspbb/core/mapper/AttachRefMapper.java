package com.jspbb.core.mapper;

import com.jspbb.core.domain.AttachRef;
import org.apache.ibatis.annotations.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 附件引用Mapper
 */
@Mapper
@Repository
public interface AttachRefMapper {
    long delete(@NotNull Long attachId, @NotNull String refType, @NotNull Long refId);

    long insert(@NotNull AttachRef record);

    @Nullable
    AttachRef select(@NotNull Long attachId, @NotNull String refType, @NotNull Long refId);

    @NotNull
    List<AttachRef> selectByAttachId(@NotNull Long attachId);

    long countByAttachId(@NotNull Long attachId);
}