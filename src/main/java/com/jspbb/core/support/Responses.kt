package com.jspbb.core.support

import org.apache.shiro.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.support.RequestContextUtils
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Responses {
    data class Body(
            /**
             * 状态码。0：通用成功；-1：通用失败；-1~-99：失败状态码；1~99：成功状态码；100~999：http失败码；1000~9999：失败状态码；100000~999999：全局错误码。
             */
            val status: Int = SUCCESS,
            /**
             * 代码。可用于国际化显示。前端如对message信息不满意，可根据code显示自己的message。
             */
            val code: String? = null,
            /**
             * 信息。错误的具体信息。也是code的国际化信息。
             */
            val message: String? = null
    )

    companion object {
        /**
         * 0-99 为成功。成功有不同状态码，可以帮助客户端做不同处理。比如评论发表成功，但需要审核；转账成功，但要2小时到账。
         * <p>
         * 成功通常不需要 message，由客户端同一显示为“成功”。
         */
        @JvmStatic
        fun ok(status: Int = SUCCESS, code: String = "ok", message: String = "OK") = ResponseEntity.ok().body(Body(status, code, message))

        @JvmStatic
        fun ok(status: Int = SUCCESS, request: HttpServletRequest, code: String, args: Array<String>? = null) = ok(status, code, getMessage(request, code, args))

        @JvmStatic
        fun badRequest(code: String = "badRequest", message: String = "Bad Request") = ResponseEntity.badRequest().body(Body(BAD_REQUEST, code, message))

        @JvmStatic
        fun badRequest(request: HttpServletRequest, code: String, args: Array<String>? = null) = badRequest(code, getMessage(request, code, args))

        @JvmStatic
        fun unauthorized(code: String = "unauthorized", message: String = "Unauthorized") = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Body(UNAUTHORIZED, code, message));

//        @JvmStatic
//        fun unauthorized() = unauthorized("unauthorized", "Unauthorized");

        /**
         * 没有数据权限。[message] 告知什么数据没有权限。
         */
        @JvmStatic
        fun forbidden(message: String = "Forbidden") = ResponseEntity.status(HttpStatus.FORBIDDEN).body(Body(FORBIDDEN, null, message))

        @JvmStatic
        fun forbidden() = forbidden("Forbidden")

        /**
         * 数据不存在。[message] 告知什么数据不存在。
         */
        @JvmStatic
        fun notFound(message: String = "Not Found") = ResponseEntity.status(HttpStatus.NOT_FOUND).body(Body(NOT_FOUND, null, message))

        /**
         * 页面找不到。用于 GET 请求。
         */
        @JvmStatic
        fun notFound(response: HttpServletResponse, message: String): String? {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, message)
            return null
        }

        /**
         * 未登录。用于 GET 请求。
         */
        @JvmStatic
        fun unauthorized(request: HttpServletRequest, response: HttpServletResponse): String? {
            WebUtils.saveRequest(request)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return "redirect:/sign_in"
        }

        /**
         * 无权限。用于 GET 请求。
         */
        @JvmStatic
        fun forbidden(response: HttpServletResponse, message: String? = null): String? {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, message)
            return null
        }

        private fun getMessage(request: HttpServletRequest, code: String, args: Array<String>?): String =
                RequestContextUtils.findWebApplicationContext(request)?.getMessage(code, args, code, RequestContextUtils.getLocale(request)) ?: code

        const val SUCCESS: Int = 0
        const val FAILURE: Int = -1;
        const val BAD_REQUEST: Int = 400
        const val UNAUTHORIZED: Int = 401
        const val FORBIDDEN: Int = 403
        const val NOT_FOUND: Int = 404
    }
}


///**
// * AJAX响应类。
// */
//open class Response(
//        /**
//         * 成功：0，无歧义成功。1，附带说明的成功，比如评论发表成功，但需要审核；转账成功，但要2小时到账。由于与用户的设置、权限等相关，也可以说是不同的成功类型。比如管理员发表的评论就可以即时显示。
//         * 请求错误：有一些公用的请求错误，比如验证码错误。
//         * 内部错误：有一些公用的内部错误，比如违反完整性约束、没有权限、没有登录。
//         *
//         * <h2>编号方式</h2>
//         *
//         * 编号有什么用？如果用于显示，给编号其实作用不是太大。如果有利于下一步行动，比如没登录跳转到登录页面，评论需要审核，就不加入页面。
//         *
//         * 编号是每个请求独立的，还是全部统一的？全部统一的编号，有利于查阅，但增加开发成本。
//         *
//         * 分为系统预定义，各自独立的，和统一可查询的。统一可查询的编号从10000开始，每个大模块给2000个预留号，每个子模块给100个预留号。
//         *
//         * <h2>程序分类</h2>
//         *
//         * 成功：
//         *     1、无条件成功，显示后自动消失，或者刷新页面，值为 0
//         *     2、有条件成功，需要给出提示，并且不消失，如评论后需要审核才能显示。值为 1-9
//         * 逻辑错误：
//         *     1、逻辑输入错误：在程序中判断为错误，如验证码错误、用户名已经存在、字符串太长。用户通过调整输入内容可以避免。
//         *     2、程序内部错误：发表信息过快、文件不可写、违反完整性约束（删除被引用数据）、无数据权限、余额不够、没有权限、没有登录。
//         *     3、不应该出现程序内部错误，所有的错误都可以让用户纠正操作来处理，包括违反完整性约束、无数据权限、余额不够、没有登录等，只有文件不可写这种本可以上传却上传不了的情况才能作为程序内部错误。而这时候应该抛出异常，查看堆栈信息。
//         * Exception 错误：
//         *     1、框架判断输入数据不合法自动抛出的异常，此种异常多为简单的输入不合法，比如不能为空的输入空，要求输入数字却输入字符。此种异常理论上不应出现，应该在输入端给予验证。
//         *     2、框架判断的未登录、无权限、页面不存在。
//         *     2、程序中未预料的错误。
//         *     3、逻辑中判断出不合法操作，以抛出异常的方式处理，比如外键约束时提示数据被某模块使用（此种方式应全局捕获异常，转成逻辑异常）。
//         *
//         * <h2>显示方式</h2>
//         *
//         * 1、无歧义成功。显示成功标识后，自动消失或刷新页面。
//         * 2、有条件成功。显示带成功标识的信息，需要保留显示信息。甚至指导下一步操作。比如评论后，是否将评论插入页面显示或者是否刷新页面。
//         * 3、逻辑错误。显示带错误标识的信息。
//         * 4、异常错误。显示带错误标识的信息。可能需要显示错误堆栈信息。
//         *
//         * 0-399 为执行成功
//         * <li> 0 执行成功
//         * <li> 1-99 执行成功，为系统预留代码
//         * <li> 100-399 执行成功，为程序特定代码
//         * 400-499 为请求错误
//         * <li> 400-449 请求错误，为系统预留代码
//         * <li> 450-499 请求错误，为系统特定代码
//         * 500-599 为内部错误
//         * <li> 500-549 内部错误，为系统预留代码
//         * <li> 550-599 内部错误，为系统特定代码
//         */
//        val code: Int = 0,
//        /**
//         * 错误信息。如[request]不为空时，会尝试使用[MessageSource]获取值，如不存在，则使用原字符串
//         */
//        var message: String = "",
//        /**
//         * 用于获取[MessageSource]和[Locale]
//         */
//        request: HttpServletRequest? = null,
//        /**
//         * [MessageSource]获取值时，使用的参数
//         */
//        args: Array<String> = emptyArray()
//) {
//    private val messageSource: MessageSource? = request?.let { RequestContextUtils.findWebApplicationContext(request) }
//    private val locale: Locale? = request?.let { RequestContextUtils.getLocale(request) }
//
//    init {
//        message = if (messageSource != null && locale != null) {
//            messageSource.getMessage(message, args, message, locale) ?: message
//        } else message
//    }
//
//    fun isSuccess() = code in 0..399
//
//    companion object {
//        fun notFound(response: HttpServletResponse, message: String): String? {
//            response.sendError(HttpServletResponse.SC_NOT_FOUND, message)
//            return null
//        }
//
//        fun unauthorized(request: HttpServletRequest, response: HttpServletResponse): String? {
//            WebUtils.saveRequest(request)
//            response.status = HttpServletResponse.SC_UNAUTHORIZED
//            return "redirect:/sign_in"
//        }
//
//        fun forbidden(response: HttpServletResponse): String? {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN)
//            return null
//        }
//
//        const val BAD_REQUEST_CODE = 400
//        const val ERROR_CODE = 500
//        const val UNAUTHORIZED_CODE = 401
//        const val UNAUTHORIZED_MESSAGE = "error.unauthorized"
//        const val FORBIDDEN_CODE = 403
//        const val FORBIDDEN_MESSAGE = "error.forbidden"
//        const val NOT_FOUND_CODE = 404
//        const val NOT_FOUND_MESSAGE = "error.notFound"
//        const val CAPTCHA_INCORRECT_CODE = 410
//        const val CAPTCHA_INCORRECT_MESSAGE = "error.captchaIncorrect"
//    }
//}
//
///** 服务器内部错误。对应 http status 500 */
//class ErrorResponse(
//        message: String,
//        request: HttpServletRequest? = null,
//        args: Array<String> = emptyArray()
//) : Response(ERROR_CODE, message, request, args) {}
//
///** 错误的请求。对应 http status 400 */
//class BadResponse(
//        message: String,
//        request: HttpServletRequest? = null,
//        args: Array<String> = emptyArray()
//) : Response(BAD_REQUEST_CODE, message, request, args) {}
//
///** 验证码错误 */
//class CaptchaIncorrectResponse(request: HttpServletRequest? = null
//) : Response(CAPTCHA_INCORRECT_CODE, CAPTCHA_INCORRECT_MESSAGE, request) {}
//
///** 未登录。对应 http status 401 */
//class UnauthorizedResponse(request: HttpServletRequest? = null
//) : Response(UNAUTHORIZED_CODE, UNAUTHORIZED_MESSAGE, request) {}
//
///** 无权访问。对应 http status 403 */
//class ForbiddenResponse(request: HttpServletRequest? = null
//) : Response(FORBIDDEN_CODE, FORBIDDEN_MESSAGE, request) {}

