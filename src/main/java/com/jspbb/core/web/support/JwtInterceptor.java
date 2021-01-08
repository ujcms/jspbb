package com.jspbb.core.web.support;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jspbb.core.domain.User;
import com.jspbb.core.service.UserService;
import com.jspbb.util.security.csrf.CsrfToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class JwtInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    public JwtInterceptor(JwtProperties properties) {
        this.properties = properties;

        Algorithm algorithm = Algorithm.HMAC256(properties.getSecret());
        long leeway = properties.getLeeway();
        this.accessTokenVerifier = JWT.require(algorithm).withIssuer(properties.getAccessTokenIssuer()).acceptLeeway(leeway).build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessToken = request.getHeader(properties.getHeaderName());
        // 使用 URL 参数方式 Token 有泄露风险，不应支持该方式
//        if (token == null) {
//            token = request.getParameter(properties.getParameterName());
//        }
        // 同时支持 RESTful Token 模式请求（无状态） 和 Cookie-Session 模式请求
        if (accessToken != null) {
            // RESTful Token 请求，使用 Token 验证身份。
            // RESTful Token 请求是无状态请求，如果已经登录则退出登录。并可防止 Cookie-Session 请求伪造成 RESTful Token 请求，逃避 CSRF 检查。
            // 使用 fetch 方法发送请求不会包含 cookie 信息，即不会包含浏览器登录信息，因此不会导致 cookie-session 退出登录。
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated() || subject.isRemembered()) {
                subject.logout();
            }
            if (accessToken.startsWith(properties.getHeaderSchema())) {
                accessToken = accessToken.substring(properties.getHeaderSchema().length());
                try {
                    DecodedJWT jwt = accessTokenVerifier.verify(accessToken);
                    Long userId = Long.parseLong(jwt.getSubject());
                    User user = userService.select(userId);
                    if (user == null) {
                        logger.debug("user id not found: " + userId);
                    } else if (user.isLocked()) {
                        logger.debug("user is locked: " + user.getUsername());
                    } else if (!user.isNormal()) {
                        logger.debug("user is disabled: " + user.getUsername());
                    } else {
                        authenticate(user.getUsername(), !JwtUtils.getRememberedClaim(jwt));
                    }
                } catch (JWTVerificationException e) {
                    // 验证失败，且过期时间超过 Remember Me 有效期。
                    logger.debug("id token JWT verification failed: " + accessToken, e);
                }
            }
            return true;
        } else {
            // 可能是Cookie-Session模式请求，需要防范 CSRF 攻击
            return CsrfToken.verify(request, response);
        }
    }

    private void authenticate(String username, boolean authenticated) {
        Subject.Builder builder = new Subject.Builder();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(username, "jwt_realm");
        // 无状态不使用 Session
        builder.sessionCreationEnabled(false).principals(principals).authenticated(authenticated);
        ThreadContext.bind(builder.buildSubject());
    }

    /**
     * JWT 相关设置
     */
    private JwtProperties properties;
    /**
     * 验证在 Access Token 是否合法
     */
    private JWTVerifier accessTokenVerifier;
    @Autowired
    private UserService userService;
}
