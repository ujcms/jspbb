package com.jspbb.core.web.processor

import com.jspbb.core.service.AnswerService
import com.jspbb.util.web.QueryParser
import com.jspbb.util.web.ThymeleafUtils
import org.springframework.stereotype.Component
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor
import org.thymeleaf.processor.element.IElementTagStructureHandler
import org.thymeleaf.templatemode.TemplateMode

@Component
class AnswersProcessor(
        private val service: AnswerService
) : AbstractAttributeTagProcessor(TemplateMode.HTML, "th", null, false, "jspbb-answers", true, 1000, true) {
    override fun doProcess(context: ITemplateContext, tag: IProcessableElementTag, attributeName: AttributeName, attributeValue: String, structureHandler: IElementTagStructureHandler) {
        val queryMap = QueryParser.getQueryMap(context)
        ThymeleafUtils.getLong(context, "questionId")?.let {
            queryMap["EQ_questionId"] = it
        }
        // 只获取状态正常的数据
        queryMap["EQ_status"] = 0

        val pageable = ThymeleafUtils.pageable(context)
        if (pageable) {
            val page = ThymeleafUtils.getPage(context)
            val pageSize = ThymeleafUtils.getPageSize(context)
            structureHandler.setLocalVariable(attributeValue, service.selectPage(queryMap, page, pageSize))
        } else {
            val offset = ThymeleafUtils.getOffset(context)
            val limit = ThymeleafUtils.getLimit(context)
            structureHandler.setLocalVariable(attributeValue, service.selectList(queryMap, offset, limit))
        }
    }
}