package com.jspbb.core.web.apicp

import com.jspbb.core.Constants.APICP
import com.jspbb.core.domain.Role
import com.jspbb.core.service.RoleService
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Converters
import com.jspbb.util.web.Entities
import com.jspbb.util.web.QueryParser
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 * 角色 Controller
 *
 * @author PONY
 */
@RestController
@RequestMapping("$APICP/roles")
class RoleController(
        private val service: RoleService
) {
    @GetMapping
    @RequiresPermissions("role:list")
    fun list(request: HttpServletRequest): Any? {
        val queryMap = QueryParser.getQueryMap(request.queryString)
        return service.selectList(queryMap)
    }

    @GetMapping("{id}")
    @RequiresPermissions("role:show")
    fun show(@PathVariable id: Long, request: HttpServletRequest): Any? {
        return service.select(id) ?: return Responses.notFound("Role id=$id not found")
    }

    @PostMapping
    @RequiresPermissions("role:create")
    fun create(@RequestBody bean: Role, request: HttpServletRequest): Any? {
        service.insert(bean)
        return Responses.ok()
    }

    @PutMapping()
    @RequiresPermissions("role:update")
    fun update(@RequestBody fields: Map<String, Any?>, request: HttpServletRequest): Any? {
        val id = Converters.getLong(fields["id"]) ?: return Responses.badRequest("Role update 'id' must not be null")
        val bean = service.select(id) ?: return Responses.notFound("Role id=$id not found")
        Entities.copy(fields, bean, "id")
        service.update(bean)
        return Responses.ok()
    }

    @PutMapping("order")
    @RequiresPermissions("role:update")
    fun updateOrder(@RequestBody ids: List<Long>, request: HttpServletRequest): Any? {
        service.updateOrder(ids)
        return Responses.ok()
    }

    @DeleteMapping
    @RequiresPermissions("role:delete")
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest): Any? {
        for (id in ids) service.delete(id)
        return Responses.ok()
    }
}
