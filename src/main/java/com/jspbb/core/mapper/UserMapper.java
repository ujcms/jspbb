package com.jspbb.core.mapper;

import com.jspbb.core.domain.User;
import com.jspbb.util.web.QueryParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    long delete(@NotNull Long id);

    long insert(@NotNull User record);

    @Nullable
    User select(@NotNull Long id);

    long update(@NotNull User record);

    @NotNull
    List<User> selectAll(@Nullable @Param("parser") QueryParser parser);

    @NotNull
    List<User> messageReceiverSuggest(@NotNull @Param("username") String username, @NotNull @Param("senderId") Long senderId);

    @Nullable
    User selectByUsername(@NotNull String username);

    @Nullable
    User selectByMobile(@NotNull String mobile);

    @Nullable
    User selectByEmail(@NotNull String email);

    @NotNull
    List<User> selectByGroupId(@NotNull Long groupId);

    @Nullable
    User selectByHome(@NotNull String home);

    @Nullable
    User selectByOpenid(@NotNull @Param("provider") String provider, @NotNull @Param("openid") String openid);

    @Nullable
    User selectByUnionid(@NotNull @Param("providerPrefix") String providerPrefix, @Param("unionid") @NotNull String unionid);
}