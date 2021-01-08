package com.jspbb.core.web.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.thymeleaf.context.IExpressionContext
import org.thymeleaf.dialect.AbstractProcessorDialect
import org.thymeleaf.dialect.IExpressionObjectDialect
import org.thymeleaf.expression.IExpressionObjectFactory
import org.thymeleaf.processor.IProcessor

@Component
class JspBBDialect : AbstractProcessorDialect("jspbb", "th", 1000), IExpressionObjectDialect {
    override fun getProcessors(dialectPrefix: String): MutableSet<IProcessor> = processor

    override fun getExpressionObjectFactory() = object : IExpressionObjectFactory {
        override fun buildObject(context: IExpressionContext, expressionObjectName: String): Any = JspBBExpression(context)

        override fun getAllExpressionObjectNames(): MutableSet<String> = mutableSetOf("jspbb")

        override fun isCacheable(expressionObjectName: String?): Boolean = false
    }

    @Autowired
    private lateinit var processor: MutableSet<IProcessor>
}