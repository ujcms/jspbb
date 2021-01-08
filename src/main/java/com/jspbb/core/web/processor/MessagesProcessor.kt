package com.jspbb.core.web.processor

import com.jspbb.core.service.MessageDetailService
import com.jspbb.core.service.MessageService
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
class MessagesProcessor(
        private val service: MessageService
) : AbstractAttributeTagProcessor(TemplateMode.HTML, "th", null, false, "jspbb-messages", true, 1000, true) {
    override fun doProcess(context: ITemplateContext, tag: IProcessableElementTag, attributeName: AttributeName, attributeValue: String, structureHandler: IElementTagStructureHandler) {
        val queryMap = QueryParser.getQueryMap(context)

//        ThymeleafUtils.getString(context, "title")?.let {
//            queryMap["Contain_1FquestionExt-title"] = it
//        }
//        ThymeleafUtils.getString(context, "markdown")?.let {
//            queryMap["Contain_1FquestionExt-markdown"] = it
//        }

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