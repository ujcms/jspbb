package com.jspbb.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.OffsetDateTime

/**
 * 用户实体类
 */
//@JsonIgnoreProperties(value = ["configs"])
open class User(
        var id: Long = Long.MIN_VALUE,
        /** 用户组ID */
        var groupId: Long = Group.DEFAULT_GROUP_ID,
        /** 用户名 */
        var username: String = "",
        /** 电子邮箱 */
        var email: String? = null,
        /** 手机号码 */
        var mobile: String? = null,
        /** 主页名 */
        var home: String? = null,
        /** 创建日期 */
        var created: OffsetDateTime = OffsetDateTime.now(),
        /** 状态 */
        var status: Int = STATUS_NORMAL,
        /** 用户扩展类 */
        open var ext: UserExt = UserExt(),
        /** 用户组 */
        open var group: Group = Group(),
        /** 角色列表 */
        open var roleList: List<Role> = emptyList(),
        /** 用户OpenID列表 */
        @JsonIgnore open var openidList: List<UserOpenid> = emptyList(),
        /** 用户限制列表 */
        @JsonIgnore open var restrictList: List<UserRestrict> = emptyList(),
        /** 配置信息列表 */
        @JsonIgnore open var configList: List<Config> = emptyList()
) {
    fun isNormal() = status == STATUS_NORMAL
    fun isLocked() = status == STATUS_LOCKED

    /** 获取用户所有的权限 */
    fun getPermissions(): Set<String> {
        // 超级管理员拥有所有权限
        if (id == 1L) return setOf("*")
        val permissions = HashSet<String>()
        for (role in roleList) {
            permissions.addAll(role.getPermList())
        }
        return permissions
    }

    fun getHomepage() = if (home != null) "/u/$home" else "/users/$id"

    /** 是否有某项前台权限。超级管理员、所有者(owner)或者拥有该权限项，则有权限。 */
    @JsonIgnore
    fun hasPerm(perm: String, ownerId: Long? = null) = id == 1L || ownerId == id || group.hasPerm(perm)

    /** 是否有某项权限 */
    @JsonIgnore
    fun hasPerm(perm: String) = id == 1L || group.hasPerm(perm)

    /** 登录错误次数 */
    @JsonIgnore
    fun getLoginErrorCount(): Long = getRestrict(RESTRICT_LOGIN_ERROR)?.count ?: 0

    /**
     * 是否需要验证码。错误次数超过[com.jspbb.core.domain.config.ConfigRestrict.passwordRetryMax]，且在[com.jspbb.core.domain.config.ConfigRestrict.passwordRetryWithin]分钟内。
     */
    @JsonIgnore
    fun isCaptchaRequired(): Boolean = !isRestrictAllowed(RESTRICT_LOGIN_ERROR, getConfigs().restrict.passwordRetryWithin, getConfigs().restrict.passwordRetryMax)

    @JsonIgnore
    fun isUploadAllowed(size: Long): Boolean = isRestrictAllowed(RESTRICT_UPLOAD, group.uploadWithin, group.uploadMax, size)

    @JsonIgnore
    fun isQuestionAllowed(): Boolean = isRestrictAllowed(RESTRICT_QUESTION, group.questionWithin, group.questionMax)

    @JsonIgnore
    fun isAnswerAllowed(): Boolean = isRestrictAllowed(RESTRICT_ANSWER, group.answerWithin, group.answerMax)

    @JsonIgnore
    fun isCommentAllowed(): Boolean = isRestrictAllowed(RESTRICT_COMMENT, group.commentWithin, group.commentMax)

    @JsonIgnore
    fun isMessageAllowed(): Boolean = isRestrictAllowed(RESTRICT_MESSAGE, group.messageWithin, group.messageMax)

    @JsonIgnore
    fun isRestrictAllowed(name: String, within: Int, max: Int, current: Long = 1): Boolean = getRestrict(name)?.let { it.lastOutOfSeconds(getConfigs().restrict.postInterval) && it.startOutOfMinutes(within, max, current) } ?: true

    @JsonIgnore
    fun getRestrict(name: String): UserRestrict? = restrictList.find { it.name == name }

    private var _configs: Configs? = null
    private fun getConfigs(): Configs {
        if (_configs == null) _configs = Configs(configList)
        return _configs ?: throw AssertionError("Set to null by another thread")
    }

    companion object {
        /** 用户表名 */
        const val TABLE_NAME = "jspbb_user"

        /** 匿名用户 ID */
        const val ANONYMOUS_ID = 1L

        /** 状态：正常 */
        const val STATUS_NORMAL = 0

        /** 状态：锁定 */
        const val STATUS_LOCKED = 1

        /** 状态：见习 */
        const val STATUS_PROBATION = 2

        /** 限制：登录错误 */
        const val RESTRICT_LOGIN_ERROR = "login_error"

        /** 限制：上传 */
        const val RESTRICT_UPLOAD = "upload"

        /** 限制：问题 */
        const val RESTRICT_QUESTION = "question"

        /** 限制：回答 */
        const val RESTRICT_ANSWER = "answer"

        /** 限制：评论 */
        const val RESTRICT_COMMENT = "comment"

        /** 限制：消息 */
        const val RESTRICT_MESSAGE = "message"
    }
}

