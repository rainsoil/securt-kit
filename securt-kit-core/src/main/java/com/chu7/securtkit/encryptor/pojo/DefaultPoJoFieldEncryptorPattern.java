package com.chu7.securtkit.encryptor.pojo;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.symmetric.DES;
import com.chu7.securtkit.config.properties.EncryptorProperties;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * Java POJO 加密方式下的默认加解密算法
 * 默认采用DES算法
 *
 * @author chu7
 * @date 2024/7/24 17:41
 */
@Slf4j
public class DefaultPoJoFieldEncryptorPattern implements FieldEncryptorStrategy<String> {

    private final EncryptorProperties encryptorProperties;
    private DES des;

    public DefaultPoJoFieldEncryptorPattern(EncryptorProperties encryptorProperties) {
        this.encryptorProperties = encryptorProperties;
    }

    @Override
    public String encryption(String cleartext) {
        //注意：这里只处理null的情况，空字符串也需要进行加密处理
        if (cleartext == null) {
            return null;
        }

        String ciphertext = cleartext;
        try {
            if (des == null) {
                des = new DES(encryptorProperties.getSecretKey().getBytes());
            }
            byte[] encryptBytes = des.encrypt(cleartext.getBytes());
            ciphertext = HexUtil.encodeHexStr(encryptBytes);
        } catch (Exception e) {
            log.error("【securt-kit】POJO模式加密失败 cleartext:{}", cleartext, e);
        }
        return ciphertext;
    }

    @Override
    public String decryption(String ciphertext) {
        //注意：这里只处理null的情况，空字符串也需要进行解密处理
        if (ciphertext == null) {
            return null;
        }

        String cleartext = ciphertext;
        try {
            if (des == null) {
                des = new DES(encryptorProperties.getSecretKey().getBytes());
            }

            byte[] decryptBytes = des.decrypt(HexUtil.decodeHex(ciphertext));
            cleartext = new String(decryptBytes);
        } catch (Exception e) {
            log.error("【securt-kit】POJO模式解密失败 ciphertext:{}", ciphertext, e);
        }
        return cleartext;
    }
}