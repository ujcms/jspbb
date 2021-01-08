package com.jspbb.util.captcha;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.primitives.Longs;
import com.jspbb.util.captcha.GmailCaptchaEngine;
import com.jspbb.util.security.Digests;
import com.octo.captcha.image.ImageCaptcha;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;

/**
 * 使用 Token 方式实现验证码。由于 RESTful 是无状态的，无法使用 Session 记住验证码。
 * <p>
 * 可以将验证码写入数据库，这种方式显得太重了。这里使用自验证的方式，不依赖数据库，甚至在分布式系统也适用。
 * <p>
 * 使用缓存记住已经使用的验证码，防止重复使用。
 */
public class CaptchaTokenService {
    public CaptchaTokenService(GmailCaptchaEngine gmailCaptchaEngine, CaptchaProperties properties) {
        this.gmailCaptchaEngine = gmailCaptchaEngine;
        this.properties = properties;
        this.cache = Caffeine.newBuilder().expireAfterWrite(properties.expires, TimeUnit.MINUTES).maximumSize(properties.maximumSize).build();

        this.algorithm = Algorithm.HMAC256(properties.secret);
        this.verifier = JWT.require(algorithm).withIssuer(properties.issuer).acceptLeeway(properties.leeway).build();
    }

    /**
     * base64 格式的图片使用方式：{@code <img src="data:image/png;base64,<base64 code>">}
     *
     * @return CaptchaToken
     */
    public CaptchaToken getCaptchaToken() {
        ImageCaptcha captcha = gmailCaptchaEngine.getNextImageCaptcha();
        BufferedImage bufferedImage = captcha.getImageChallenge();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", outputStream);
            String base64 = new String(Base64.encodeBase64(outputStream.toByteArray()));
            // 区分大小写
            String word = captcha.getResponse();
            Date now = new Date();
            // 只取秒级，不要毫秒。JWT 的 expiresAt 只能记录到秒
            Date expiresAt = new Date(now.getTime() / 1000 * 1000 + properties.expires * 60 * 1000);
            String subject = sign(word, expiresAt);
            String token = JWT.create().withIssuer(properties.getIssuer()).withIssuedAt(now).withExpiresAt(expiresAt).withSubject(subject).sign(algorithm);
            return new CaptchaToken(token, properties.expires * 60, base64);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 用于前台检查验证码是否正确，提高用户体验，方便。尝试次数限制一般为 5 次。
     *
     * @param token   captcha token
     * @param captcha 验证码
     * @return 是否验证成功
     */
    public boolean tryCaptcha(String token, String captcha) {
        if (StringUtils.isBlank(token) || StringUtils.isBlank(captcha)) return false;
        try {
            DecodedJWT jwt = verifier.verify(token);
            int wordLength = StringUtils.length(captcha);
            if (wordLength < gmailCaptchaEngine.getMinWordLength() || wordLength > gmailCaptchaEngine.getMaxWordLength()) return false;
            Integer count = cache.getIfPresent(token);
            if (count == null) count = 0;
            // count == -1 代表已经被使用过
            if (count < 0 || count >= properties.maxTryCount) return false;
            // 区分大小写
            if (verifySubject(jwt.getSubject(), captcha, jwt.getExpiresAt())) return true;
            cache.put(token, count + 1);
            return false;
        } catch (JWTVerificationException e) {
            // 验证失败
            return false;
        }
    }

    /**
     * 检查验证码是否正确，无论正确与否，该验证码立即失效，不可以再次使用。
     *
     * @param token   captcha token
     * @param captcha 验证码
     * @return 是否验证成功
     */
    public boolean validateCaptcha(String token, String captcha) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            Integer count = cache.getIfPresent(token);
            // 已使用或者尝试过多，直接返回false
            if (count != null && (count < 0 || count >= properties.maxTryCount)) return false;
            // count == -1 代表已经被使用过
            cache.put(token, -1);
            return verifySubject(jwt.getSubject(), captcha, jwt.getExpiresAt());
        } catch (JWTVerificationException e) {
            // 验证失败
            return false;
        }
    }

    private boolean verifySubject(String subject, String word, Date expiresAt) {
        return sign(word, expiresAt).equals(subject);
    }

    private String sign(String word, Date expiresAt) {
        // 不区分大小写
        byte[] subjectBytes = algorithm.sign(word.toUpperCase(ENGLISH).getBytes(UTF_8), Longs.toByteArray(expiresAt.getTime()));
        return Base64.encodeBase64URLSafeString(subjectBytes);
    }

//    /**
//     * 获取验证码和过期时间的字符串组合
//     *
//     * @param word      验证码
//     * @param expiresAt 过期日期
//     * @return {@code word.toUpperCase(Locale.ENGLISH) + ":" + expiresAt.toInstant().getEpochSecond()}
//     */
//    private String getContent(String word, Date expiresAt) {
//        if (word == null) word = "";
//        // 只能取到秒，不能取毫秒数，因为 JWT 的 expiresAt 不包含毫秒信息
//        return word.toUpperCase(ENGLISH) + ":" + expiresAt.toInstant().getEpochSecond();
//    }

    private Cache<String, Integer> cache;
    private GmailCaptchaEngine gmailCaptchaEngine;
    private CaptchaProperties properties;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @ConfigurationProperties(prefix = "captcha")
    public static class CaptchaProperties {
        /**
         * 加密密钥。默认生成一个32位随机字符串。集群时需要指定一个固定的密钥。
         */
        private String secret = Digests.randomSecret(32);
        private String issuer = "jspBB/captcha";
        private long leeway = 3 * 60;
        /**
         * 过期时间。单位：分钟。默认15分钟。
         */
        private int expires = 15;
        /**
         * 最大 Captcha 数量。默认 100,000
         */
        private long maximumSize = 1_000_000;
        /**
         * 最大可尝试次数。
         */
        private int maxTryCount = 5;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public long getLeeway() {
            return leeway;
        }

        public void setLeeway(long leeway) {
            this.leeway = leeway;
        }

        public int getExpires() {
            return expires;
        }

        public void setExpires(int expires) {
            this.expires = expires;
        }

        public long getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
        }

        public int getMaxTryCount() {
            return maxTryCount;
        }

        public void setMaxTryCount(int maxTryCount) {
            this.maxTryCount = maxTryCount;
        }
    }

    public static class CaptchaToken {
        public CaptchaToken() {
        }

        public CaptchaToken(String token, int expiresIn, String base64) {
            this.token = token;
            this.expiresIn = expiresIn;
            this.base64 = base64;
        }

        private String token;
        private int expiresIn;
        private String base64;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getBase64() {
            return base64;
        }

        public void setBase64(String base64) {
            this.base64 = base64;
        }
    }
}
