package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.*
import com.jspbb.core.domain.User.Companion.RESTRICT_ANSWER
import com.jspbb.core.domain.User.Companion.RESTRICT_COMMENT
import com.jspbb.core.domain.User.Companion.RESTRICT_LOGIN_ERROR
import com.jspbb.core.domain.User.Companion.RESTRICT_MESSAGE
import com.jspbb.core.domain.User.Companion.RESTRICT_QUESTION
import com.jspbb.core.domain.User.Companion.RESTRICT_UPLOAD
import com.jspbb.core.listener.UserDeleteListener
import com.jspbb.core.mapper.*
import com.jspbb.util.ip.IpSeeker
import com.jspbb.util.security.CredentialsDigest
import com.jspbb.util.security.Digests
import com.jspbb.util.web.QueryParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

/**
 * 用户Service
 */
@Service
class UserService(
        private val credentialsDigest: CredentialsDigest,
        private val ipSeeker: IpSeeker,
        private val configService: ConfigService,
        private val seqService: SeqService,
        private val userRoleMapper: UserRoleMapper,
        private val restrictMapper: UserRestrictMapper,
        private val openidMapper: UserOpenidMapper,
        private val extMapper: UserExtMapper,
        private val mapper: UserMapper
) {
    @Lazy
    @Autowired(required = false)
    private var deleteListeners: List<UserDeleteListener>? = null

    fun select(id: Long): User? = mapper.select(id)

    @Transactional
    fun insert(record: User, ext: UserExt, vararg openids: UserOpenid) {
        insert(record, ext, emptyList(), openids.toList())
    }

    @Transactional
    fun insert(record: User, ext: UserExt, roleIds: List<Long> = emptyList(), openids: List<UserOpenid> = emptyList()) {
        record.id = seqService.getNextVal(User.TABLE_NAME)
        ext.id = record.id
        ext.salt = Digests.randomSalt(UserExt.SALT_BYTE_LENGTH)
        if (ext.rowPassword != null) {
            ext.password = credentialsDigest.digest(ext.rowPassword, ext.salt)!!
        }
        val region = ipSeeker.find(ext.ip)
        ext.ipCountry = region.country
        ext.ipProvince = region.province
        ext.ipCity = region.city
        record.ext = ext
        mapper.insert(record)
        extMapper.insert(ext)
        roleIds.forEach { userRoleMapper.insert(UserRole(record.id, it)) }
        openids.forEach { it.userId = record.id; openidMapper.insert(it) }
        record.openidList = openids
    }

    @Transactional
    fun insert(openid: UserOpenid) {
        openidMapper.insert(openid)
    }

    @Transactional
    fun update(record: User, ext: UserExt, roleIds: List<Long>? = null): Long {
        if (roleIds != null) {
            userRoleMapper.deleteByUserId(record.id)
            roleIds.forEach { roleId -> userRoleMapper.insert(UserRole(record.id, roleId)) }
        }
        update(ext)
        return mapper.update(record)
    }

    @Transactional
    fun update(record: User): Long {
        return mapper.update(record)
    }

    @Transactional
    fun update(ext: UserExt): Long {
        if (ext.rowPassword != null) {
            ext.salt = Digests.randomSalt(UserExt.SALT_BYTE_LENGTH)
            ext.password = credentialsDigest.digest(ext.rowPassword, ext.salt)!!
        }
        return extMapper.update(ext)
    }

    @Transactional
    fun updatePassword(ext: UserExt, newPassword: String) {
        ext.rowPassword = newPassword
        ext.salt = Digests.randomSalt(UserExt.SALT_BYTE_LENGTH)
        if (ext.rowPassword != null) {
            ext.password = credentialsDigest.digest(ext.rowPassword, ext.salt)!!
        }
        extMapper.update(ext)
    }

    @Transactional
    fun delete(id: Long): Long {
        return select(id)?.let {
            firePreDeleteDeleteListeners(id)
            restrictMapper.deleteByUserId(id)
            openidMapper.deleteByUserId(id)
            userRoleMapper.deleteByUserId(id)
            extMapper.delete(id)
            mapper.delete(id)
        } ?: 0
    }

    @Transactional
    fun loginSuccess(ext: UserExt, ip: String) {
        val region = ipSeeker.find(ip)
        ext.loginSuccess(ip, region.country, region.province, region.city)
        update(ext)
        restrictMapper.select(ext.id, RESTRICT_LOGIN_ERROR)?.let { updateRestrict(it, 0) }
    }

    @Transactional
    fun loginFailure(user: User) {
        val configs = configService.selectConfigs()
        addUpRestrict(user, RESTRICT_LOGIN_ERROR, configs.restrict.passwordRetryWithin)
    }

    @Transactional
    fun addUpUpload(user: User, length: Long) {
        user.ext.uploadedLength += length
        update(user.ext)
        addUpRestrict(user, RESTRICT_UPLOAD, user.group.uploadWithin, length)
    }

    @Transactional
    fun addUpQuestion(user: User) = addUpRestrict(user, RESTRICT_QUESTION, user.group.questionWithin)

    @Transactional
    fun addUpAnswer(user: User) = addUpRestrict(user, RESTRICT_ANSWER, user.group.answerWithin)

    @Transactional
    fun addUpComment(user: User) = addUpRestrict(user, RESTRICT_COMMENT, user.group.commentWithin)

    @Transactional
    fun addUpMessage(user: User) = addUpRestrict(user, RESTRICT_MESSAGE, user.group.messageWithin)

    @Transactional
    fun addUpRestrict(user: User, name: String, within: Int, current: Long = 1) {
        val restrict = restrictMapper.select(user.id, name)
        if (restrict != null) {
            val count = if (restrict.startWithinMinutes(within)) restrict.count + current else current;
            updateRestrict(restrict, count)
        } else {
            insertRestrict(user.id, name, current, OffsetDateTime.now())
        }
    }

    @Transactional
    fun updateRestrict(restrict: UserRestrict, count: Long) {
        restrict.count = count
        restrict.last = OffsetDateTime.now()
        restrictMapper.update(restrict)
    }

    @Transactional
    fun insertRestrict(userId: Long, name: String, count: Long, start: OffsetDateTime) = restrictMapper.insert(UserRestrict(userId, name, count, start, start))

    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<User> {
        val parser = QueryParser(queryMap, "id_desc")
        return PageHelper.startPage<User>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<User> {
        val parser = QueryParser(queryMap, "id_desc")
        return if (limit == null) {
            mapper.selectAll(parser)
        } else {
            PageHelper.offsetPage<User>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }

    fun messageReceiverSuggest(username: String, senderId: Long, offset: Int = 0, limit: Int? = null): List<User> {
        return if (limit == null) {
            mapper.messageReceiverSuggest(username, senderId)
        } else {
            PageHelper.offsetPage<Question>(offset, limit, false).doSelectPage { mapper.messageReceiverSuggest(username, senderId) }
        }
    }

    fun selectByUsername(username: String): User? = mapper.selectByUsername(username)
    fun selectByMobile(mobile: String): User? = mapper.selectByMobile(mobile)
    fun selectByEmail(email: String): User? = mapper.selectByEmail(email)
    fun selectByGroupId(groupId: Long): List<User> = mapper.selectByGroupId(groupId)
    fun selectByHome(home: String): User? = mapper.selectByHome(home)

    fun selectByOpenid(provider: String, openid: String): User? = mapper.selectByOpenid(provider, openid)

    fun selectByUnionid(providerPrefix: String, unionid: String): User? = mapper.selectByUnionid("$providerPrefix%", unionid)

    private fun firePreDeleteDeleteListeners(id: Long) {
        deleteListeners?.forEach { it.preUserDelete(id) }
    }

}
