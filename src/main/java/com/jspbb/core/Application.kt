package com.jspbb.core

import com.jspbb.util.captcha.CaptchaServlet
import org.apache.catalina.startup.ContextConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.*
import org.springframework.web.WebApplicationInitializer
import javax.servlet.ServletContext
import javax.servlet.SessionTrackingMode
import javax.servlet.http.HttpServlet


/**
 * 应用启动入口
 *
 * Created by PONY on 2017/4/29.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@ImportResource("classpath:config/captcha.xml", "classpath:config/shiro.xml", "classpath:config/**/context*.xml")
@Import(ContextConfig::class)
//@PropertySource("classpath:config/spring.properties")
class Application : SpringBootServletInitializer(), WebApplicationInitializer {
    /**
     * 设置 ServletContext
     */
    override fun onStartup(servletContext: ServletContext) {
        // Session跟踪模式。为了避免url带;jsessionid，不使用url模式。Session tracking modes (one or more of the following: "cookie", "url", "ssl").
        servletContext.setSessionTrackingModes(setOf(SessionTrackingMode.COOKIE))
        // Session HttpOnly Cookie。防止 js 获取 Session 的 Cookie。
        servletContext.sessionCookieConfig.isHttpOnly = true
        super.onStartup(servletContext)
    }

    /**
     * war方式启动的处理方法
     */
    override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
        return configureApplication(builder)
    }

    /**
     * 验证码
     */
    @Bean
    fun captchaServletRegistration(): ServletRegistrationBean<HttpServlet> {
        val registration = ServletRegistrationBean<HttpServlet>(CaptchaServlet(), "/captcha")
        registration.setName("captchaServlet")
        return registration
    }

    companion object {
        /**
         * jar方式启动的处理方法
         * @param args
         */
        @JvmStatic
        fun main(args: Array<String>) {
            configureApplication(SpringApplicationBuilder()).run(*args)
        }

        /**
         * war方式启动和jar方式启动共用的配置
         * @param builder
         * @return
         */
        fun configureApplication(builder: SpringApplicationBuilder): SpringApplicationBuilder {
            return builder.sources(Application::class.java)
        }
    }
}

///**
// * jar方式启动的处理方法
// * @param args
// */
//fun main(args: Array<String>) {
//    configureApplication(SpringApplicationBuilder()).run(*args)
//}
//
///**
// * war方式启动和jar方式启动共用的配置
// * @param builder
// * @return
// */
//fun configureApplication(builder: SpringApplicationBuilder): SpringApplicationBuilder {
//    return builder.sources(Application::class.java)
//}