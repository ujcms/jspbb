package com.jspbb.core.service

import com.jspbb.core.domain.Seq
import com.jspbb.core.mapper.SeqMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * 序列Service。用于生成主键值，使用数据库表代替数据库序列的功能，可跨数据库平台。
 */
@Service
class SeqService(
        /** 缓存大小 */
        @Value("\${sequence-cache-size}")
        private val cacheSize: Int,
        private val mapper: SeqMapper
) {
    /**
     * 获取下一个序列值
     *
     * @param name 序列名称
     */
    @Synchronized
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun getNextVal(name: String): Long {
        var seq = seqMap[name]
        if (seq == null) {
            seq = selectAndUpdate(name)
            seqMap[name] = seq
        }
        if (seq.isCacheEmpty()) {
            seq = selectAndUpdate(name)
            seqMap[name] = seq
        }
        return seq.nextCachedVal()!!
    }

    /**
     * 查询并更新序列
     */
    private fun selectAndUpdate(name: String): Seq {
        var seq = mapper.selectForUpdate(name)
        if (seq != null) {
            seq.makeCache(cacheSize)
            mapper.update(seq)
        } else {
            seq = Seq(name)
            seq.makeCache(cacheSize)
            mapper.insert(seq)
        }
        return seq
    }

    companion object {
        /** 序列Map */
        val seqMap = HashMap<String, Seq>()
    }
}
