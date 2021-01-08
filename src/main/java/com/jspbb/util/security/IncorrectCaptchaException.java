package com.jspbb.util.security;

import org.apache.shiro.authc.CredentialsException;

/**
 * 验证码错误异常
 * 
 * @author liufang
 * 
 */
public class IncorrectCaptchaException extends CredentialsException {
	private static final long serialVersionUID = 1L;
}
