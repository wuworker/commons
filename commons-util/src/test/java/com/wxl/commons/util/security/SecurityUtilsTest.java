package com.wxl.commons.util.security;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.util.Base64;

import static com.wxl.commons.util.security.SecurityUtils.*;


/**
 * Created by wuxingle on 2017/9/11 0011.
 * hash，加解密测试
 */
public class SecurityUtilsTest {

    @Test
    public void testdoMD5() throws Exception {
        String pass = "123456";
        byte[] bytes = doMD5(pass.getBytes());

        print(bytes);
    }

    @Test
    public void testdoSHA1() throws Exception {
        String pass = "123456";
        byte[] bytes = doSHA1(pass.getBytes());

        print(bytes);
    }

    @Test
    public void testdoSHA256() throws Exception {
        String pass = "123456";
        byte[] bytes = doSHA256(pass.getBytes());

        print(bytes);
    }

    @Test
    public void testdoSHA512() throws Exception {
        String pass = "123456";
        byte[] bytes = doSHA512(pass.getBytes());

        print(bytes);
    }

    @Test
    public void testdoMACWithMD5() throws Exception {
        String pass = "123456";
        String key = "abc123";
        byte[] bytes = doMACWithMD5(pass.getBytes(), key.getBytes());

        print(bytes);
    }

    @Test
    public void testdoMACWithSHA1() throws Exception {
        String pass = "123456";
        String key = "abc123";

        byte[] bytes = doMACWithSHA1(pass.getBytes(), key.getBytes());

        print(bytes);
    }

    @Test
    public void testdoMACWithSHA256() throws Exception {
        String pass = "123456";
        String key = "abc123";

        byte[] bytes = doMACWithSHA256(pass.getBytes(), key.getBytes());
        print(bytes);
    }

    @Test
    public void testdoMACWithSHA512() throws Exception {
        String pass = "123456";
        String key = "abc123";

        byte[] bytes = doMACWithSHA512(pass.getBytes(), key.getBytes());
        print(bytes);
    }

    @Test
    public void testdoAES() throws Exception {
        String pass = "123456";
        byte[] key = KeyGeneratorUtils.generateAESKey();
        System.out.println("密钥长度:" + key.length * 8);
        System.out.println(Hex.encodeHexString(key));

        byte[] bytes = doAESEncode(pass.getBytes(), key);
        print(bytes);

        String source = new String(doAESDecode(bytes, key));

        System.out.println(source);
    }

    @Test
    public void testdoRSA() throws Exception {
        String pass = "123456";
        KeyPair keyPair = KeyGeneratorUtils.generateRSAKey1024();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();

        //公钥加密
        byte[] encBytes = doRSAPublicKeyEncode(pass.getBytes(), publicKey);
        print(encBytes);

        //私钥解密
        String s1 = new String(doRSAPrivateKeyDecode(encBytes, privateKey));
        System.out.println(s1);

        System.out.println("-----------------------------------------------");

        //私钥加密
        encBytes = doRSAPrivateKeyEncode(pass.getBytes(), privateKey);
        print(encBytes);
        //公钥解密
        String s2 = new String(doRSAPublicKeyDecode(encBytes, publicKey));
        System.out.println(s2);
    }

    private void print(byte[] bytes) {
        System.out.println(bytes.length);
        System.out.println(Hex.encodeHexString(bytes));
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }
}