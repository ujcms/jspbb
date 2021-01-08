package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.Group
import com.jspbb.core.domain.Role
import com.jspbb.core.mapper.GroupMapper
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
class GroupService(
        private val seqService: SeqService,
        private val mapper: GroupMapper
) {
    fun select(id: Long): Group? = mapper.select(id)

    @Transactional
    fun insert(record: Group) {
        record.id = seqService.getNextVal(Group.TABLE_NAME)
        mapper.insert(record)
    }

    @Transactional
    fun update(record: Group): Long {
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

    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<Group> {
        val parser = QueryParser(queryMap, "type,order,id")
        return PageHelper.startPage<Group>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<Group> {
        val parser = QueryParser(queryMap, "type,order,id")
        return if (limit == null) {
            mapper.selectAll(parser)
        } else {
            PageHelper.offsetPage<Group>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }
}
