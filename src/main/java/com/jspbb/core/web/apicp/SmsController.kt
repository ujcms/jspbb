package com.jspbb.core.web.apicp

import com.jspbb.core.Constants.APICP
import com.jspbb.core.Constants.validPage
import com.jspbb.core.Constants.validPageSize
import com.jspbb.core.service.SmsService
import com.jspbb.core.support.Responses
import com.jspbb.util.web.QueryParser
import com.jspbb.util.web.Servlets.springPage
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 * 短信 Controller
 *
 * @author PONY
 */
@RestController("apicpSmsController")
@RequestMapping("$APICP/sms")
class SmsController(
        private val service: SmsService
) {
    @GetMapping
    @RequiresPermissions("sms:list")
    fun list(page: Int?, pageSize: Int?, request: HttpServletRequest): Any? {
        val queryMap = QueryParser.getQueryMap(request.queryString)
        return springPage(service.selectPage(queryMap, validPage(page), validPageSize(pageSize)))
    }

    @GetMapping("{id}")
    @RequiresPermissions("sms:show")
    fun show(@PathVariable id: Long, request: HttpServletRequest): Any? {
        return service.select(id) ?: return Responses.notFound("Sms id=$id not found")
    }

    data class BeanParam(val id: Long?, val bean: Map<String, Any?>?, val ext: Map<String, Any?>?, val roleIds: List<Long>?)

    @DeleteMapping
    @RequiresPermissions("sms:delete")
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest): Any? {
        for (id in ids) service.delete(id)
        return Responses.ok()
    }

}
