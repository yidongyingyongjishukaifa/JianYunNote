package com.henu.jianyunnote.util;

import junit.framework.TestCase;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class AESUtilTest {

    @Test
    public void encrypt() throws UnsupportedEncodingException {
        String content = "847923197@qq.com";
        //字节补全
        if (AESUtil.AES_TYPE.equals("AES/ECB/NoPadding")) {
            System.out.println();
            content = AESUtil.completionCodeFor16Bytes(content);
            System.out.println("加密内容补全后: " + content);
        }
        // 加密
        String encryptResult = AESUtil.encrypt(content);
        content = new String(encryptResult);
        TestCase.assertEquals("+JCcBigYXAROhX4+DttzkHtIa6F3rCmkMB6953wx6Bo=", content);
    }

    @Test
    public void decrypt() {
        String content = "";
        String encryptResult = "+JCcBigYXAROhX4+DttzkHtIa6F3rCmkMB6953wx6Bo=";
        // 解密
        String decryptResult = AESUtil.decrypt(encryptResult);
        content = new String(decryptResult);
        //还原
        if (AESUtil.AES_TYPE.equals("AES/ECB/NoPadding")) {
            content = AESUtil.resumeCodeOf16Bytes(content);
        }
        TestCase.assertEquals("847923197@qq.com", content);
    }
}