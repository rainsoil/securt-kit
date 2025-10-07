package com.chu7.securtkit.encryptor.pojo;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.symmetric.AES;
import com.chu7.securtkit.config.properties.EncryptorProperties;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import lombok.extern.slf4j.Slf4j;
/**
 * Java POJO 加密方式下的AES加解密算法
 *
 * @author chu7
 * @date 2024/7/24 17:41
 */
@Slf4j
public class AesPoJoFieldEncryptorStrategy implements FieldEncryptorStrategy<String> {

    private final EncryptorProperties encryptorProperties;
    private AES aes;

    public AesPoJoFieldEncryptorStrategy(EncryptorProperties encryptorProperties) {
        this.encryptorProperties = encryptorProperties;
    }

    @Override
    public String encryption(String cleartext) {
        if (cleartext == null) {
            return null;
        }

        String ciphertext = cleartext;
        try {
            if (aes == null) {
                // AES密钥长度必须是16、24或32字节
                String key = ensureKeyLength(encryptorProperties.getSecretKey(), 16);
                aes = new AES(key.getBytes());
            }
            byte[] encryptBytes = aes.encrypt(cleartext.getBytes());
            ciphertext = HexUtil.encodeHexStr(encryptBytes);
        } catch (Exception e) {
            log.error("【securt-kit】AES加密失败 cleartext:{}", cleartext, e);
        }
        return ciphertext;
    }

    @Override
    public String decryption(String ciphertext) {
        if (ciphertext == null) {
            return null;
        }

        String cleartext = ciphertext;
        try {
            if (aes == null) {
                String key = ensureKeyLength(encryptorProperties.getSecretKey(), 16);
                aes = new AES(key.getBytes());
            }

            byte[] decryptBytes = aes.decrypt(HexUtil.decodeHex(ciphertext));
            cleartext = new String(decryptBytes);
        } catch (Exception e) {
            log.error("【securt-kit】AES解密失败 ciphertext:{}", ciphertext, e);
        }
        return cleartext;
    }

    /**
     * 确保密钥长度符合要求
     */
    private String ensureKeyLength(String key, int requiredLength) {
        if (key == null) {
            key = "default-key-1234567890123456";
        }
        
        if (key.length() == requiredLength) {
            return key;
        } else if (key.length() > requiredLength) {
            return key.substring(0, requiredLength);
        } else {
            // 如果密钥长度不足，则用0填充
            StringBuilder sb = new StringBuilder(key);
            while (sb.length() < requiredLength) {
                sb.append("0");
            }
            return sb.toString();
        }
    }
}