package com.jspbb.core.web.apicp

import com.jspbb.core.Constants.APICP
import com.jspbb.core.domain.Group
import com.jspbb.core.service.GroupService
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Converters
import com.jspbb.util.web.Entities
import com.jspbb.util.web.QueryParser
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 * 用户组 Controller
 *
 * @author PONY
 */
@RestController
@RequestMapping("$APICP/groups")
class GroupController(
        private val service: GroupService
) {
    @GetMapping
    @RequiresPermissions("group:list")
    fun list(request: HttpServletRequest): Any? {
        val queryMap = QueryParser.getQueryMap(request.queryString)
        return service.selectList(queryMap)
    }

    @GetMapping("{id}")
    @RequiresPermissions("group:show")
    fun show(@PathVariable id: Long, request: HttpServletRequest): Any? {
        return service.select(id) ?: return Responses.notFound("Group id=$id not found")
    }

    @PostMapping
    @RequiresPermissions("group:create")
    fun create(@RequestBody bean: Group, request: HttpServletRequest): Any? {
        service.insert(bean)
        return Responses.ok()
    }

    @PutMapping()
    @RequiresPermissions("group:update")
    fun update(@RequestBody fields: Map<String, Any?>, request: HttpServletRequest): Any? {
        val id = Converters.getLong(fields["id"]) ?: return Responses.badRequest("Group update 'id' must not be null")
        val bean = service.select(id) ?: return Responses.notFound("Group id=$id not found")
        Entities.copy(fields, bean, "id")
        service.update(bean)
        return Responses.ok()
    }

    @PutMapping("order")
    @RequiresPermissions("group:update")
    fun updateOrder(@RequestBody ids: List<Long>, request: HttpServletRequest): Any? {
        service.updateOrder(ids)
        return Responses.ok()
    }

    @DeleteMapping
    @RequiresPermissions("group:delete")
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest): Any? {
        // 10以内是预定义及0级用户组，不能删除
        for (id in ids) if (id > 10) service.delete(id)
        return Responses.ok()
    }
}
