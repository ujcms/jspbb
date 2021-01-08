package com.jspbb.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * 角色实体类
 */
open class Role(
        var id: Long = Long.MIN_VALUE,
        /** 名称 */
        var name: String = "",
        /** 描述 */
        var description: String? = null,
        /** 权限值 */
        var perms: String? = null,
        /** 排序 */
        var order: Int = 999999
) {
    // 权限要除去空串，否则 shiro 会报错
    @JsonIgnore
    fun getPermList(): List<String> = perms?.split(";")?.filter { it.isNotBlank() } ?: emptyList()

    companion object {
        /** 角色表名 */
        const val TABLE_NAME = "jspbb_role"
    }
}