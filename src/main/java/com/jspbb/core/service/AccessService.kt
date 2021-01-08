package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.Access
import com.jspbb.core.domain.AccessExt
import com.jspbb.core.domain.User
import com.jspbb.core.listener.UserDeleteListener
import com.jspbb.core.mapper.AccessExtMapper
import com.jspbb.core.mapper.AccessMapper
import com.jspbb.util.ip.IpSeeker
import com.jspbb.util.web.QueryParser
import eu.bitwalker.useragentutils.DeviceType
import eu.bitwalker.useragentutils.UserAgent
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.MalformedURLException
import java.net.URL

/**
 * 访问Service
 */
@Service
class AccessService(
        private val ipSeeker: IpSeeker,
        private val seqService: SeqService,
        private val extMapper: AccessExtMapper,
        private val mapper: AccessMapper
) : UserDeleteListener {
    fun select(id: Long): Access? = mapper.select(id)

    @Transactional
    fun insert(url: String, referrer: String?, userAgent: String?, ip: String, cookie: Long, user: User?) {
        val access = Access(ip = ip, cookie = cookie, userId = user?.id, user = user, ext = AccessExt(url = url, referrer = referrer, userAgent = userAgent))
        insert(access, access.ext)
    }

    @Transactional
    fun insert(record: Access, ext: AccessExt) {
        record.ext = ext
        val region = ipSeeker.find(record.ip)
        record.country = region.country
        record.province = region.province
        record.city = region.city

        if (ext.userAgent?.isNotBlank() == true) {
            val ua = UserAgent.parseUserAgentString(ext.userAgent)
            record.browser = ua.browser.toString()
            record.os = ua.operatingSystem.toString()
            record.device = when (ua.operatingSystem.deviceType) {
                // 桌面电脑
                DeviceType.COMPUTER -> '1'
                // 智能手机
                DeviceType.MOBILE -> '2'
                // 平板电脑
                DeviceType.TABLET -> '3'
                // 游戏机
                DeviceType.GAME_CONSOLE -> '4'
                // 电视机顶盒
                DeviceType.DMR -> '5'
                // 智能手表、智能眼镜
                DeviceType.WEARABLE -> '6'
                // 未知
                else -> '0'
            }
        }

        if (StringUtils.isNoneBlank(ext.url, ext.referrer)) {
            try {
                val accessURL = URL(ext.url)
                val referrerURL = URL(ext.referrer)
                // url和referrer的域名不同，则代表来源不同网站，设置来源域名
                if (!StringUtils.equals(referrerURL.host, accessURL.host)) {
                    var source = referrerURL.protocol + "://" + referrerURL.host
                    if (referrerURL.port >= 0) {
                        source += ":" + referrerURL.port
                    }
                    record.source = source
                }
            } catch (e: MalformedURLException) {
                logger.error("url: ${ext.url}; referrer: ${ext.referrer}", e)
            }
        }

        record.id = seqService.getNextVal(Access.TABLE_NAME)
        ext.id = record.id
        mapper.insert(record)
        extMapper.insert(ext)
    }

    @Transactional
    fun update(record: Access, ext: AccessExt): Long {
        extMapper.update(ext)
        return mapper.update(record)
    }

    @Transactional
    fun delete(id: Long): Long {
        extMapper.delete(id)
        return mapper.delete(id)
    }

    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<Access> {
        val parser = QueryParser(queryMap, "id_desc")
        return PageHelper.startPage<Access>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<Access> {
        val parser = QueryParser(queryMap, "id_desc")
        return if (limit == null) {
            mapper.selectAll(parser)
        } else {
            PageHelper.offsetPage<Access>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }

    override fun preUserDelete(id: Long) {
        mapper.updateUserId(id, User.ANONYMOUS_ID)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(AccessService::class.java)
    }
}
