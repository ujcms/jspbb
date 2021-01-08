package com.jspbb.core.mapper;

import com.jspbb.core.domain.Access;
import com.jspbb.core.domain.Sms;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 访问Mapper
 */
@Mapper
@Repository
public interface AccessMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Access record);

    @Nullable
    Access select(@NotNull Long id);

    long update(@NotNull Access record);
    @NotNull
    List<Access> selectAll(@Nullable @Param("parser") QueryParser parser);

    long updateUserId(@NotNull @Param("origUserId") Long origUserId, @NotNull @Param("userId") Long userId);
}