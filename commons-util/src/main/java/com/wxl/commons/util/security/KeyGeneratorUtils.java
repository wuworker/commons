package com.wxl.commons.util.security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Created by wuxingle on 2017/9/10 0010.
 * 密钥生成工具类
 */
public class KeyGeneratorUtils {

    private KeyGeneratorUtils() {
    }

    /**
     * HmacMD5密钥
     */
    public static byte[] generateMACWithMD5Key() {
        return generateKey("HmacMD5");
    }

    /**
     * HmacSHA1密钥
     */
    public static byte[] generateMACWithSHA1Key() {
        return generateKey("HmacSHA1");
    }

    /**
     * HmacSHA256密钥
     */
    public static byte[] generateMACWithSHA256Key() {
        return generateKey("HmacSHA256");
    }

    /**
     * HmacSHA512密钥
     */
    public static byte[] generateMACWithSHA512Key() {
        return generateKey("HmacSHA512");
    }


    /**
     * AES密钥
     */
    public static byte[] generateAESKey() {
        return generateKey("AES");
    }


    /**
     * RSA密钥对
     * 1024
     */
    public static KeyPair generateRSAKey1024() {
        return generateKeyPair("RSA", 1024);
    }

    /**
     * RSA密钥对
     * 2048
     */
    public static KeyPair generateRSAKey2048() {
        return generateKeyPair("RSA", 2048);
    }


    /**
     * 产生key
     */
    private static byte[] generateKey(String algorithm) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 产生密钥对
     */
    private static KeyPair generateKeyPair(String algorithm, int len) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(len);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


}
