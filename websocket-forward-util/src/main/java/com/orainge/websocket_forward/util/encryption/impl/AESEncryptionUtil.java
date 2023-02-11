package com.orainge.websocket_forward.util.encryption.impl;

import com.orainge.websocket_forward.util.encryption.EncryptionUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * AES加解密工具
 *
 * @author orainge
 */
@Component
@ConditionalOnMissingBean(EncryptionUtil.class)
public class AESEncryptionUtil implements EncryptionUtil {
    @Override
    public String encrypt(String str, String key) {
        try {
            return aesEncrypt(str, key);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String decrypt(String str, String key) {
        try {
            return aesDecrypt(str, key);
        } catch (Exception e) {
            return null;
        }
    }

    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        /*防止linux下 随机生成key*/
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(encryptKey.getBytes());
        keyGenerator.init(128, random);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES"));

        byte[] encryptBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(encryptBytes);
    }

    public static String aesDecrypt(String encryptStr, String decryptKey) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(decryptKey.getBytes());
            kgen.init(128, random);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
            byte[] decryptBytes = cipher.doFinal(Base64.decodeBase64(encryptStr));
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (Exception ignore) {
        }
        return "";
    }
}