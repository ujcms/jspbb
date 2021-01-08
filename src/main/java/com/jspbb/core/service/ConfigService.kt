package com.jspbb.core.service

import com.jspbb.core.domain.Config
import com.jspbb.core.domain.Configs
import com.jspbb.core.mapper.ConfigMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 配置Service
 */
@Service
class ConfigService(
        private val mapper: ConfigMapper
) {
    @Transactional
    fun delete(name: String): Long = mapper.delete(name)

    @Transactional
    fun insert(record: Config): Long = mapper.insert(record)

    fun select(name: String): Config? = mapper.select(name)

    @Transactional
    fun update(record: Config): Long = mapper.update(record)

    @Transactional
    fun deleteByNamePrefix(namePrefix: String): Long = mapper.deleteByNamePrefix("$namePrefix%")

    @Transactional
    fun updateByNamePrefix(namePrefix: String, map: Map<String, String?>) {
        deleteByNamePrefix(namePrefix)
        for ((k, v) in map) {
            insert(Config(namePrefix + k, v))
        }
    }

    fun selectAll(): List<Config> = mapper.selectAll()

    fun selectConfigs() = Configs(selectAll())
}