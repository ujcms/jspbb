package com.jspbb.core.mapper;

import com.jspbb.core.domain.UserOpenid;
import org.apache.ibatis.annotations.Mapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserOpenidMapper {
    long insert(@NotNull UserOpenid record);

    long update(@NotNull UserOpenid record);

    @NotNull
    List<UserOpenid> selectByUserId(@NotNull Long userId);

    long deleteByUserId(@NotNull Long userId);
}