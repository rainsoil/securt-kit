package com.chu7.securtkit.encryptor.pojo;

import cn.hutool.core.codec.Base64;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import lombok.extern.slf4j.Slf4j;
/**
 * Java POJO 加密方式下的Base64编解码算法
 * 注意：Base64只是编码，不是真正的加密，安全性较低
 *
 * @author chu7
 * @date 2024/7/24 17:41
 */
@Slf4j
public class Base64PoJoFieldEncryptorStrategy implements FieldEncryptorStrategy<String> {

    @Override
    public String encryption(String cleartext) {
        if (cleartext == null) {
            return null;
        }

        String encoded = cleartext;
        try {
            encoded = Base64.encode(cleartext);
        } catch (Exception e) {
            log.error("【securt-kit】Base64编码失败 cleartext:{}", cleartext, e);
        }
        return encoded;
    }

    @Override
    public String decryption(String ciphertext) {
        if (ciphertext == null) {
            return null;
        }

        String decoded = ciphertext;
        try {
            decoded = Base64.decodeStr(ciphertext);
        } catch (Exception e) {
            log.error("【securt-kit】Base64解码失败 ciphertext:{}", ciphertext, e);
        }
        return decoded;
    }
}