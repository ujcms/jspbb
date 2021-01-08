package com.jspbb.core.web.apicp

import com.jspbb.core.Constants.APICP
import com.jspbb.core.Constants.validPage
import com.jspbb.core.Constants.validPageSize
import com.jspbb.core.domain.User
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Entities
import com.jspbb.util.web.QueryParser
import com.jspbb.util.web.Servlets
import com.jspbb.util.web.Servlets.springPage
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 * 用户 Controller
 *
 * @author PONY
 */
@RestController
@RequestMapping("$APICP/users")
class UserController(
        private val service: UserService
) {
    @GetMapping
    @RequiresPermissions("user:list")
    fun list(page: Int?, pageSize: Int?, request: HttpServletRequest): Any? {
        val queryMap = QueryParser.getQueryMap(request.queryString)
        return springPage(service.selectPage(queryMap, validPage(page), validPageSize(pageSize)))
    }

    @GetMapping("{id}")
    @RequiresPermissions("user:show")
    fun show(@PathVariable id: Long, request: HttpServletRequest): Any? {
        return service.select(id) ?: return Responses.notFound("User id=$id not found")
    }

    data class BeanParam(val id: Long?, val bean: Map<String, Any?>?, val ext: Map<String, Any?>?, val roleIds: List<Long>?)

    @PostMapping
    @RequiresPermissions("user:create")
    fun create(@RequestBody params: BeanParam, request: HttpServletRequest): Any? {
        val bean = User()
        val ext = bean.ext
        Entities.copy(params.bean, bean, "id")
        Entities.copy(params.ext, ext, "id")
        service.insert(bean, ext, params.roleIds ?: emptyList())
        return Responses.ok()
    }

    @PutMapping
    @RequiresPermissions("user:update")
    fun update(@RequestBody params: BeanParam, request: HttpServletRequest): Any? {
        val id = params.id ?: return Responses.badRequest("User update 'id' must not be null")
        val bean = service.select(id) ?: return Responses.notFound("User id=$id not found")
        val ext = bean.ext
        Entities.copy(params.bean, bean, "id")
        Entities.copy(params.ext, ext, "id")
        service.update(bean, ext, params.roleIds ?: emptyList())
        return Responses.ok()
    }


    @DeleteMapping
    @RequiresPermissions("user:delete")
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest): Any? {
        for (id in ids) service.delete(id)
        return Responses.ok()
    }

    @GetMapping("username-validation")
    fun usernameValidation(request: HttpServletRequest): Any? {
        val username = Servlets.getParam(request.queryString, "username") ?: return false
        return service.selectByUsername(username) == null
    }

    @GetMapping("email-validation")
    fun emailValidation(request: HttpServletRequest): Any? {
        val email = Servlets.getParam(request.queryString, "email") ?: return false
        return service.selectByEmail(email) == null
    }

    @GetMapping("mobile-validation")
    fun mobileValidation(request: HttpServletRequest): Any? {
        val mobile = Servlets.getParam(request.queryString, "mobile") ?: return false
        return service.selectByMobile(mobile) == null
    }
}
