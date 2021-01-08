package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.User
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.util.web.Servlets
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 用户 Controller
 *
 * @author PONY
 */
@RestController("apiUserController")
@RequestMapping("$API/users")
class UserController(
        private val service: UserService
) {
    @GetMapping("/current_user")
    fun currentUser(): User? {
        val username = Contexts.getUsername()
        if (username != null) {
            val user = service.selectByUsername(username)
            if (user?.isNormal() == true) return user
        }
        return null
    }

    @GetMapping("username_not_exist")
    fun usernameNotExist(request: HttpServletRequest, response: HttpServletResponse): Any {
        // 防止中文用户名出现乱码
        val username = Servlets.getParam(request.queryString, "username")
        return username != null && service.selectByUsername(username) == null
    }

    @GetMapping("mobile_not_exist")
    fun mobileNotExist(mobile: String?, request: HttpServletRequest, response: HttpServletResponse): Any {
        return mobile != null && service.selectByMobile(mobile) == null
    }

    @GetMapping("email_not_exist")
    fun emailNotExist(email: String?, request: HttpServletRequest, response: HttpServletResponse): Any {
        return email != null && service.selectByEmail(email) == null
    }

    @GetMapping("username_exist")
    fun usernameExist(request: HttpServletRequest, response: HttpServletResponse): Any {
        // 防止中文用户名出现乱码
        val username = Servlets.getParam(request.queryString, "username")
        return username != null && service.selectByUsername(username) != null
    }

    @GetMapping("mobile_exist")
    fun mobileExist(mobile: String?, request: HttpServletRequest, response: HttpServletResponse): Any {
        return mobile != null && service.selectByMobile(mobile) != null
    }

    @GetMapping("email_exist")
    fun emailExist(email: String?, request: HttpServletRequest, response: HttpServletResponse): Any {
        return email != null && service.selectByEmail(email) != null
    }
}
