package com.jspbb.core.web.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jspbb.core.domain.User;
import com.jspbb.core.service.UserService;
import com.jspbb.core.web.support.JwtProperties;
import com.jspbb.core.web.support.JwtUtils;
import com.jspbb.util.security.HashCredentialsDigest;
import com.jspbb.util.web.Servlets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.jspbb.core.Constants.API;
import static com.jspbb.core.web.support.JwtProperties.*;

@RestController
@RequestMapping(API + "/auth/jwt")
public class JwtAuthController {
    private static Logger logger = LoggerFactory.getLogger(JwtAuthController.class);

    public JwtAuthController(JwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(properties.getSecret());
        long leeway = properties.getLeeway();
        this.refreshTokenVerifier = JWT.require(algorithm).withIssuer(properties.getRefreshTokenIssuer()).acceptLeeway(leeway).build();
    }

    static class LoginParam {
        public String username;
        public String password;
        // 是否浏览器访问
        public boolean isBrowser = true;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginParam params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.selectByUsername(params.username);
        if (user == null) {
            // 用户不存在
            result.put("status", "error");
            return result;
        } else if (!credentialsDigest.matches(user.getExt().getPassword(), params.password, user.getExt().getSalt())) {
            // 密码错误
            result.put("status", "error");
            return result;
        } else if (user.isLocked()) {
            // 用户被锁定
            result.put("status", "error");
            return result;
        } else if (!user.isNormal()) {
            // 非正常用户
            result.put("status", "error");
            return result;
        }
        // 生成 Access Token
        Date now = new Date();
        String loginId = UUID.randomUUID().toString().replace("-", "");
        String accessToken = createAccessToken(loginId, user.getId(), now, false);
        String refreshToken = createRefreshToken(loginId, now, user.getId(), now, params.isBrowser);
        result.put(ACCESS_TOKEN, accessToken);
        result.put(EXPIRES_IN, properties.getExpiresSeconds());
        result.put(REMEMBERED, false);
        result.put(REFRESH_TOKEN, refreshToken);
        result.put(REFRESH_EXPIRES_IN, properties.getRefreshExpires());
        result.put(REFRESH_AUTH_EXPIRES_IN, params.isBrowser ? properties.getExpiresSeconds() : properties.getRefreshAuthExpiresSeconds());
        result.put("currentAuthority", user.getPermissions());
        result.put("status", "ok");
        userService.loginSuccess(user.getExt(), Servlets.getRemoteAddr(request));
        return result;
    }

    static class RefreshTokenParam {
        public String refreshToken;
        // 是否浏览器访问
        public boolean isBrowser = true;
    }

    @PostMapping("/refresh_token")
    public Map<String, Object> refreshToken(@RequestBody RefreshTokenParam params) {
        try {
            String refreshToken = params.refreshToken;
            DecodedJWT jwt = refreshTokenVerifier.verify(refreshToken);
            // 可以获取 jwtId 从数据库等存储空间中验证 token 是否伪造
            Long userId = Long.parseLong(jwt.getSubject());
            User user = userService.select(userId);
            if (user == null || !user.isNormal()) return null;
            Date now = new Date();
            String loginId = JwtUtils.getLoginIdClaim(jwt);
            Date loginTime = JwtUtils.getLoginTimeClaim(jwt);
            long authExpiresMillisAt = JwtUtils.getAuthExpiresAtClaim(jwt).getTime();
            boolean remembered = now.getTime() > authExpiresMillisAt;
            // 生成 Access Token
            String accessToken = createAccessToken(JwtUtils.getLoginIdClaim(jwt), userId, now, remembered);
            // 如果 Refresh Token 认证有效期未过期，且小于有效期，则续期 Refresh Token
            long refreshExpiresIn = properties.getRefreshExpiresSeconds() - (now.getTime() - loginTime.getTime()) / 1000;
            long refreshAuthExpiresIn = params.isBrowser ? properties.getExpiresSeconds() : properties.getRefreshExpiresSeconds();
            if (refreshAuthExpiresIn > refreshExpiresIn) refreshAuthExpiresIn = refreshExpiresIn;
            Map<String, Object> result = new HashMap<>();
            result.put(ACCESS_TOKEN, accessToken);
            result.put(EXPIRES_IN, properties.getExpiresSeconds());
            result.put(REMEMBERED, remembered);
            if (authExpiresMillisAt > now.getTime() && authExpiresMillisAt < jwt.getExpiresAt().getTime()) {
                refreshToken = createRefreshToken(loginId, loginTime, userId, now, params.isBrowser);
                // 只有 refresh token 刷新了才传递以下参数
                result.put(REFRESH_EXPIRES_IN, refreshExpiresIn);
                result.put(REFRESH_AUTH_EXPIRES_IN, refreshAuthExpiresIn);
            }
            result.put(REFRESH_TOKEN, refreshToken);
            result.put("currentAuthority", user.getPermissions());
            result.put("status", "ok");
            return result;
        } catch (JWTVerificationException e) {
            // 验证失败
            logger.debug("refresh token JWT verification failed: " + params.refreshToken, e);
            return null;
        }
    }

    private String createAccessToken(String loginId, Long userId, Date now, boolean remembered) {
        Date expiresAt = new Date(now.getTime() + properties.getExpiresMillis());
        JWTCreator.Builder builder = JWT.create().withSubject(userId.toString()).withIssuedAt(now).withExpiresAt(expiresAt).withIssuer(properties.getAccessTokenIssuer());
        JwtUtils.withLoginIdClaim(builder, loginId);
        JwtUtils.withRememberedClaim(builder, remembered);
        return builder.sign(algorithm);
    }

    private String createRefreshToken(String loginId, Date loginTime, Long userId, Date now, boolean isBrowser) {
        Date expiresAt = new Date(loginTime.getTime() + properties.getRefreshExpiresMillis());
        // Access Token 有效期较短（如30分钟），需要使用 Refresh Token 维持登录状态，因此可以用于记录、监控用户活动状态
        JWTCreator.Builder builder = JWT.create().withSubject(userId.toString()).withIssuer(properties.getRefreshTokenIssuer()).withIssuedAt(now).withExpiresAt(expiresAt);
        // 如果是浏览器访问则 Refresh Token 认证有效期与 Access Token 有效期一样
        Date authExpiresAt = new Date(now.getTime() + (isBrowser ? properties.getExpiresMillis() : properties.getRefreshAuthExpiresMillis()));
        if (authExpiresAt.getTime() > expiresAt.getTime()) authExpiresAt = expiresAt;
        JwtUtils.withAuthExpiresAtClaim(builder, authExpiresAt);
        JwtUtils.withLoginIdClaim(builder, loginId);
        JwtUtils.withLoginTimeClaim(builder, loginTime);
        return builder.sign(algorithm);
    }

    private JwtProperties properties;
    private Algorithm algorithm;
    /**
     * 验证 Refresh Token
     */
    private JWTVerifier refreshTokenVerifier;
    @Autowired
    private UserService userService;
    @Autowired
    private HashCredentialsDigest credentialsDigest;

}
