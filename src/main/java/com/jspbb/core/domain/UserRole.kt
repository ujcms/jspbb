package com.jspbb.core.domain

/**
 * 用户角色关联实体类
 */
data class UserRole(
        /** 用户ID */
        var userId: Long = Long.MIN_VALUE,
        /** 角色ID */
        var roleId: Long = Long.MIN_VALUE
)