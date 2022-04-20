package com.jiayi.common.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * aes 对称加密工具，使用AES/CBC/ISO10126Padding
 * Created by cjw on 2020/2/9.
 *
 * @author cjw
 */
@Slf4j
public class AesUtil {
    /**
     * 字符串编码
     */
    private static final String BYTES_ENCODING = "UTF-8";
    /**
     * 密钥
     */
    private static final String SECRET_KEY = "wzJ1lZThZoZg4i97";
    /**
     * IV
     */
    private static final String IV = "2b6SFVfhEmHyqtJg";
    /**
     * 加密算法 模式 填充方法
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    /**
     *
     */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * iv加密参数
     */
    private static IvParameterSpec ivParameterSpec;

    /**
     * 密钥加密对象
     */
    private static SecretKeySpec keySpec;

    static {
        try {
            ivParameterSpec = new IvParameterSpec(IV.getBytes(BYTES_ENCODING));
            keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), KEY_ALGORITHM);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 加密操作 base64 url安全
     *
     * @param plainText 待加密值
     * @return 加密值
     */
    public static String encryptUrlSafe(Object plainText) {
        try {
            byte[] encryptResult = encrypt(plainText.toString().getBytes(BYTES_ENCODING));
            if (encryptResult != null) {
                return BaseEncoding.base64Url().encode(encryptResult);
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException ex) {
            return null;
        } catch (Exception e) {
            log.error("encrypt error", e);
            return null;
        }
    }

    /**
     * 解密操作 base64 url安全
     *
     * @param cipherText 待解密值
     * @return 解密后的值
     */
    public static String decryptUrlSafe(String cipherText) {
        try {
            byte[] plainTextBytes = decrypt(BaseEncoding.base64Url().decode(cipherText));
            if (plainTextBytes != null) {
                return new String(plainTextBytes, BYTES_ENCODING);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("decryptUrlSafe error", e);
            return null;
        }
    }

    /**
     * 解密操作
     *
     * @param cipherTextBytes 待解密值byte
     * @return 解密后的值byte
     */
    private static byte[] decrypt(byte[] cipherTextBytes) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
            return cipher.doFinal(cipherTextBytes);
        } catch (Exception ex) {
            log.error("decrypt error", ex);
            return null;
        }
    }

    /**
     * 加密操作
     *
     * @param plainText 待加密bytes
     * @return 加密后的bytes
     */
    private static byte[] encrypt(byte[] plainText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            return cipher.doFinal(plainText);
        } catch (Exception ex) {
            log.error("encrypt error", ex);
            return null;
        }
    }

    /**
     * 检验 参数 是否能正常解密 base64 url safe
     *
     * @param aesArgs String
     * @return boolean
     */
    public static boolean isCanDecryptUrlSafe(String aesArgs) {
        try {
            if (Strings.isNullOrEmpty(aesArgs)) {
                return false;
            }
            String args = AesUtil.decryptUrlSafe(aesArgs);
            if (Strings.isNullOrEmpty(args)) {
                return false;
            }
        } catch (Exception ex) {
            log.warn("Decrypt failed: ", ex);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(AesUtil.encryptUrlSafe("R0001"));
        System.out.println(AesUtil.encryptUrlSafe("R0002"));
        System.out.println(AesUtil.encryptUrlSafe("ceo"));
        System.out.println(AesUtil.encryptUrlSafe("coo"));

        List<Integer> arrayList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        arrayList = arrayList.subList(1,arrayList.size());
        System.out.println(arrayList);
        System.out.println(System.currentTimeMillis()/1000);
    }

}
