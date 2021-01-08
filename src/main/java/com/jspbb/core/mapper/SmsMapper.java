package com.jspbb.core.mapper;

import com.jspbb.core.domain.Sms;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Mapper
@Repository
public interface SmsMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull Sms record);

    @Nullable
    Sms select(@NotNull Long id);

    long update(@NotNull Sms record);

    @NotNull
    List<Sms> selectAll(@Nullable @Param("parser") QueryParser parser);

    long countByIp(@NotNull @Param("ip") String ip, @NotNull @Param("startDate") OffsetDateTime startDate, @NotNull @Param("type") String type);
}