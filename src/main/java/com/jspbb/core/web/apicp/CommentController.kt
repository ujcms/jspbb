package com.jspbb.core.web.apicp

import com.jspbb.core.Constants.APICP
import com.jspbb.core.Constants.validPage
import com.jspbb.core.Constants.validPageSize
import com.jspbb.core.service.CommentService
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Converters
import com.jspbb.util.web.Entities
import com.jspbb.util.web.QueryParser
import com.jspbb.util.web.Servlets.springPage
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 * 评论 Controller
 *
 * @author PONY
 */
@RestController("apicpCommentController")
@RequestMapping("$APICP/comments")
class CommentController(
        private val service: CommentService
) {
    @GetMapping
    @RequiresPermissions("comment:list")
    fun list(page: Int?, pageSize: Int?, request: HttpServletRequest): Any? {
        val queryMap = QueryParser.getQueryMap(request.queryString)
        return springPage(service.selectPage(queryMap, validPage(page), validPageSize(pageSize)))
    }

    @GetMapping("{id}")
    @RequiresPermissions("comment:show")
    fun show(@PathVariable id: Long, request: HttpServletRequest): Any? {
        return service.select(id) ?: return Responses.notFound("Comment id=$id not found")
    }

    @PutMapping
    @RequiresPermissions("comment:update")
    fun update(@RequestBody fields: Map<String, Any?>, request: HttpServletRequest): Any? {
        val id = Converters.getLong(fields["id"]) ?: return Responses.badRequest("Comment update 'id' must not be null")
        val bean = service.select(id) ?: return Responses.notFound("Comment id=$id not found")
        Entities.copy(fields, bean, "id")
        service.update(bean)
        return Responses.ok()
    }

    @DeleteMapping
    @RequiresPermissions("comment:delete")
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest): Any? {
        for (id in ids) service.delete(id)
        return Responses.ok()
    }
}
