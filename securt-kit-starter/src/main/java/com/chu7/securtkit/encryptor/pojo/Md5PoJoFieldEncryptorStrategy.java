package com.chu7.securtkit.encryptor.pojo;

import cn.hutool.crypto.digest.MD5;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import lombok.extern.slf4j.Slf4j;
/**
 * Java POJO 加密方式下的MD5哈希算法
 * 注意：MD5是单向加密，无法解密，解密时返回原值
 * 适用于密码等不需要解密的敏感信息
 *
 * @author chu7
 * @date 2024/7/24 17:41
 */
@Slf4j
public class Md5PoJoFieldEncryptorStrategy implements FieldEncryptorStrategy<String> {

    private final MD5 md5 = new MD5();

    @Override
    public String encryption(String cleartext) {
        if (cleartext == null) {
            return null;
        }

        String hashed = cleartext;
        try {
            hashed = md5.digestHex(cleartext);
        } catch (Exception e) {
            log.error("【securt-kit】MD5加密失败 cleartext:{}", cleartext, e);
        }
        return hashed;
    }

    @Override
    public String decryption(String ciphertext) {
        // MD5是单向加密，无法解密
        log.warn("【securt-kit】MD5是单向加密算法，无法解密，返回原密文");
        return ciphertext;
    }
}