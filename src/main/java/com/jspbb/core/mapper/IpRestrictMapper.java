package com.jspbb.core.mapper;

import com.jspbb.core.domain.IpRestrict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface IpRestrictMapper {
    long delete(@NotNull String ip, @NotNull String name);

    long insert(@NotNull IpRestrict record);

    @Nullable
    IpRestrict select(@NotNull @Param("ip") String ip, @NotNull  @Param("name")String name);

    long update(@NotNull IpRestrict record);

    @NotNull
    List<IpRestrict> selectByIp(@NotNull String ip);

    long deleteByIp(@NotNull String ip);
}