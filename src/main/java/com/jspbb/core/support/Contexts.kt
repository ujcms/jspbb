package com.jspbb.core.support

import org.apache.shiro.SecurityUtils

class Contexts {
    companion object {
        @JvmStatic
        fun getUsernameAuthenticated(): String? {
            val subject = SecurityUtils.getSubject()
            val principal = subject.principal
            return if (principal is String && subject.isAuthenticated) principal else null
        }

        @JvmStatic
        fun getUsername(): String? {
            val subject = SecurityUtils.getSubject()
            val principal = subject.principal
            return if (principal is String && (subject.isRemembered || subject.isAuthenticated)) principal else null
        }
    }
}