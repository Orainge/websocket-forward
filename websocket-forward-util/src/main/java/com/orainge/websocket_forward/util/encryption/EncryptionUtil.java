package com.orainge.websocket_forward.util.encryption;

/**
 * 加解密工具接口
 *
 * @author orainge
 */
public interface EncryptionUtil {
    String encrypt(String str, String key);

    String decrypt(String str, String key);
}
