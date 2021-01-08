package com.jspbb.util.security;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 支持SHA-1/MD5消息摘要的工具类.
 * <p>
 * 返回ByteSource，可进一步被编码为Hex, Base64或UrlSafeBase64
 */
public class Digests {
    /**
     * 对输入字符串进行sha1散列.
     */
    public static byte[] sha1(byte[] input) {
        return digest(input, SHA1, null);
    }

    public static byte[] sha1(byte[] input, byte[] salt) {
        return digest(input, SHA1, salt);
    }

    /**
     * 对输入字符串进行sha256散列.
     */
    public static byte[] sha256(byte[] input) {
        return digest(input, SHA256, null);
    }

    public static byte[] sha256(byte[] input, byte[] salt) {
        return digest(input, SHA256, salt);
    }

    /**
     * 对输入字符串进行sha512散列.
     */
    public static byte[] sha512(byte[] input) {
        return digest(input, SHA512, null);
    }

    public static byte[] sha512(byte[] input, byte[] salt) {
        return digest(input, SHA512, salt);
    }

    /**
     * 对字符串进行散列, 支持md5与sha1算法.
     */
    private static byte[] digest(byte[] input, String algorithm, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            if (salt != null) digest.update(salt);
            return digest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成随机的byte[]作为salt.
     *
     * @param count byte数组的大小
     */
    public static byte[] randomSaltByte(int count) {
        Validate.isTrue(count > 0, "The count argument must be a positive integer (1 or larger)");

        byte[] bytes = new byte[count];
        random.nextBytes(bytes);
        return bytes;
    }

    @NotNull
    public static String randomSalt(int count) {
//        return Hex.encodeHexString(randomSaltByte(count));
        return RandomStringUtils.random(count, 0, 0, true, true, null, random);
    }

    @NotNull
    public static String randomSecret(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, random);
    }

    @NotNull
    public static byte[] secretToBytes(String secret) {
        Validate.notNull(secret, "The secret cannot be null");
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 对文件进行md5散列.
     */
    public static byte[] md5(InputStream input) throws IOException {
        return digest(input, MD5);
    }

    public static byte[] md5(byte[] input, byte[] salt) {
        return digest(input, MD5, salt);
    }

    /**
     * 对文件进行sha1散列.
     */
    public static byte[] sha1(InputStream input) throws IOException {
        return digest(input, SHA1);
    }

    private static byte[] digest(InputStream input, String algorithm) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            int bufferLength = 8 * 1024;
            byte[] buffer = new byte[bufferLength];
            int read = input.read(buffer, 0, bufferLength);

            while (read > -1) {
                messageDigest.update(buffer, 0, read);
                read = input.read(buffer, 0, bufferLength);
            }

            return messageDigest.digest();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String SHA1 = "SHA-1";
    private static final String SHA256 = "SHA-256";
    private static final String SHA512 = "SHA-512";
    private static final String MD5 = "MD5";

    private static SecureRandom random = new SecureRandom();
}
