package com.jspbb.core.web.apicp

import com.jspbb.core.Constants.APICP
import com.jspbb.core.Constants.validPage
import com.jspbb.core.Constants.validPageSize
import com.jspbb.core.domain.Question
import com.jspbb.core.service.QuestionService
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Entities
import com.jspbb.util.web.QueryParser
import com.jspbb.util.web.Servlets.springPage
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 * 问题 Controller
 *
 * @author PONY
 */
@RestController("apicpQuestionController")
@RequestMapping("$APICP/questions")
class QuestionController(
        private val service: QuestionService
) {
    @GetMapping
    @RequiresPermissions("question:list")
    fun list(page: Int?, pageSize: Int?, request: HttpServletRequest): Any? {
        val queryMap = QueryParser.getQueryMap(request.queryString)
        return springPage(service.selectPage(queryMap, validPage(page), validPageSize(pageSize)))
    }

    @GetMapping("{id}")
    @RequiresPermissions("question:show")
    fun show(@PathVariable id: Long, request: HttpServletRequest): Any? {
        return service.select(id) ?: return Responses.notFound("Question id=$id not found")
    }

    data class BeanParam(val id: Long?, val bean: Map<String, Any?>?, val ext: Map<String, Any?>?)

    @PutMapping
    @RequiresPermissions("question:update")
    fun update(@RequestBody params: BeanParam, request: HttpServletRequest): Any? {
        val id = params.id ?: return Responses.badRequest("Question update 'id' must not be null")
        val bean = service.select(id) ?: return Responses.notFound("Question id=$id not found")
        val ext = bean.ext
        Entities.copy(params.bean, bean, "id")
        Entities.copy(params.ext, ext, "id")
        service.update(bean, ext)
        return Responses.ok()
    }

    @DeleteMapping
    @RequiresPermissions("question:delete")
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest): Any? {
        for (id in ids) service.delete(id)
        return Responses.ok()
    }
}
