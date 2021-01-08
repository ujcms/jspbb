package com.jspbb.core

import com.jspbb.core.mapper.IpRestrictMapper
import com.jspbb.core.web.support.FrontendInterceptor
import com.jspbb.core.web.support.IpBlacklistInterceptor
import com.jspbb.core.web.support.JwtInterceptor
import com.jspbb.core.web.support.JwtProperties
import com.jspbb.util.security.csrf.CsrfInterceptor
import com.jspbb.util.web.*
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.mobile.device.LiteDeviceResolver
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.filter.DelegatingFilterProxy
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import java.time.*
import java.util.*
import javax.servlet.Filter

/**
 *
 */
@Configuration
@MapperScan("com.jspbb.core.mapper")
class ContextConfig {
    // 使用配置文件代替
//    @Bean
//    fun vendorDatabaseIdProvider() = VendorDatabaseIdProvider().apply {
//        setProperties(mapOf("MySQL" to "mysql","Oracle" to "oracle", "SQL Server" to "sqlserver", "DB2" to "db2", "PostgreSQL" to "postgres", "H2" to "h2").toProperties())
//    }

    // 使用 Interceptor 代替
//    @Bean
//    fun csrfFilter(): FilterRegistrationBean<Filter> {
//        val filterRegistration = FilterRegistrationBean<Filter>()
//        filterRegistration.filter = CsrfFilter("/api")
//        filterRegistration.isEnabled = true
//        filterRegistration.addUrlPatterns("/*");
//        return filterRegistration;
//    }

    @Bean
    fun shiroFilterRegistrationBean(): FilterRegistrationBean<Filter> {
        val filterRegistration = FilterRegistrationBean<Filter>()
        filterRegistration.filter = DelegatingFilterProxy("shiroFilter")
        filterRegistration.isEnabled = true
        filterRegistration.addInitParameter("targetFilterLifecycle", "true")
        filterRegistration.addUrlPatterns("/*")
        return filterRegistration
    }

    @Bean
    fun jspDispatcherFilterRegistrationBean(): FilterRegistrationBean<Filter> {
        val filterRegistration = FilterRegistrationBean<Filter>()
        filterRegistration.filter = JspDispatcherFilter()
        filterRegistration.isEnabled = true
        filterRegistration.addUrlPatterns("*.jsp")
        filterRegistration.addUrlPatterns("*.jspx")
        return filterRegistration
    }


    @Configuration
    class WebConfig : WebMvcConfigurer {
        /**
         * IP黑名单拦截器
         */
        @Bean
        fun ipBlacklistInterceptor() = IpBlacklistInterceptor()
        /**
         * 计时器拦截器
         */
        @Bean
        fun timerInterceptor() = TimerInterceptor()

        /**
         * 前台拦截器
         */
        @Bean
        fun frontendInterceptor() = FrontendInterceptor()

        /**
         * 国际化语言修改拦截器
         */
        @Bean
        fun localeChangeInterceptor() = LocaleChangeInterceptor()

        /**
         * CSRF 拦截器
         */
        fun csrfInterceptor() = CsrfInterceptor()

        /**
         * JWT 配置
         */
        @Bean
        fun jwtProperties() = JwtProperties()

        /**
         * JWT 拦截器
         */
        @Bean
        fun jwtInterceptor() = JwtInterceptor(jwtProperties())

        @Bean
        fun localeResolver(): LocaleResolver {
            val cookieLocaleResolver = CookieLocaleResolver();
            cookieLocaleResolver.cookieName = LocaleChangeInterceptor.DEFAULT_PARAM_NAME;
            cookieLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE)
            return cookieLocaleResolver;
        }

//        /**
//         * WebSocket Endpoint. 使用内嵌式Tomcat时需要改配置，使用独立Tomcat时则不需要。
//         */
//        @Bean
//        fun serverEndpointExporter(): ServerEndpointExporter {
//            return ServerEndpointExporter()
//        }

        override fun addInterceptors(registry: InterceptorRegistry) {
            // 错误页面和带点的文件请求 jquery.js bootstrap.min.css 都不经过拦截器
            registry.addInterceptor(timerInterceptor()).excludePathPatterns("/error/**", "/**/*.*")
            registry.addInterceptor(localeChangeInterceptor()).excludePathPatterns("/error/**", "/**/*.*")
            // IP黑名单拦截
            registry.addInterceptor(ipBlacklistInterceptor()).excludePathPatterns("/error/**", "/**/*.*")
            // RESTful 有自己的机制防止 CSRF 攻击
            registry.addInterceptor(csrfInterceptor()).excludePathPatterns("/error/**", "/**/*.*", "/api/**")
            registry.addInterceptor(frontendInterceptor()).excludePathPatterns("/error/**", "/**/*.*", "/api/**")
            // RESTful 接口地址以 /api 开头
            registry.addInterceptor(jwtInterceptor()).addPathPatterns("/api/**")
            super.addInterceptors(registry)
        }
    }

    @ControllerAdvice
    class GlobalControllerAdvice {
        @InitBinder
        fun initBinder(binder: WebDataBinder) {
            binder.registerCustomEditor(LocalDateTime::class.java, LocalDateTimeEditor())
            binder.registerCustomEditor(LocalDate::class.java, LocalDateEditor())
            binder.registerCustomEditor(OffsetDateTime::class.java, OffsetDateTimeEditor())
            binder.registerCustomEditor(ZonedDateTime::class.java, ZonedDateTimeEditor())
            binder.registerCustomEditor(Instant::class.java, InstantEditor())
            // 将空字符串转换为 null，以符合 oracle varchar2 会自动将空字符串转为 null 的特征。
            binder.registerCustomEditor(String::class.java, StringEmptyEditor())
        }
    }


    @Configuration
    @EnableWebSocketMessageBroker
    class WebSocketStompConfig : WebSocketMessageBrokerConfigurer {
        override fun registerStompEndpoints(registry: StompEndpointRegistry) {
            registry.addEndpoint("/portfolio").setAllowedOrigins("*").withSockJS();
//            registry.addEndpoint("/portfolio").withSockJS();
        }

        override fun configureMessageBroker(config: MessageBrokerRegistry) {
            // 全局使用的消息前缀（客户端订阅路径上会体现出来）
            config.setApplicationDestinationPrefixes("/app");
            // 广播地址和点对点地址
            config.enableSimpleBroker("/topic", "/queue");
            // 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
            // registry.setUserDestinationPrefix("/user/");
        }

//        override fun configureClientInboundChannel(registration: ChannelRegistration) {
////            registration.interceptors();
//        }
    }

    /**
     * 设备识别器，用于识别是否手机访问
     *
     * @return
     */
    @Bean
    fun liteDeviceResolver(): LiteDeviceResolver = LiteDeviceResolver()
}