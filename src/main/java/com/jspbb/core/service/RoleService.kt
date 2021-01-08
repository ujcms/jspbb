package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.Role
import com.jspbb.core.mapper.RoleMapper
import com.jspbb.util.web.QueryParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 角色 Service
 *
 * @author PONY
 */
@Service
class RoleService(
        private val seqService: SeqService,
        private val mapper: RoleMapper
) {
    fun select(id: Long): Role? = mapper.select(id)

    @Transactional
    fun insert(record: Role) {
        record.id = seqService.getNextVal(Role.TABLE_NAME)
        mapper.insert(record)
    }

    @Transactional
    fun update(record: Role): Long {
        return mapper.update(record)
    }

    @Transactional
    fun updateOrder(ids: List<Long>) {
        for ((index, value) in ids.withIndex()) {
            mapper.updateOrder(value, index)
        }
    }

    @Transactional
    fun delete(id: Long): Long {
        return select(id)?.let { mapper.delete(id) } ?: 0
    }

    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<Role> {
        val parser = QueryParser(queryMap, "order,id")
        return PageHelper.startPage<Role>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<Role> {
        val parser = QueryParser(queryMap, "order,id")
        return if (limit == null) {
            mapper.selectAll(parser)
        } else {
            PageHelper.offsetPage<Role>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }
}
