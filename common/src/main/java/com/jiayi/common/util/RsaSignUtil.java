package com.jiayi.common.util;

import com.google.common.io.BaseEncoding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * RSA 签名工具类
 * Created on 2020/12/4.
 *
 * @author
 */
@Slf4j
public final class RsaSignUtil {

    /**
     * 签名类型
     */
    private static final String SIGN_TYPE_RSA = "RSA";

    /**
     * 签名算法规则
     */
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    private static final String FIELD_SIGN = "sign";

    private static final int FIELD_SIGN_MAX_LENGTH = 128;
    /**
     * 私钥 - 仅调试使用
     */
    private static final String PRIVATE_KEY =
            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJYAxxhei12DtMPv" +
                    "xnsfE+9ww9N97jTnwoFt6BxaHyGgetPDGmjwUai4zMFhLRdOj4ENS4pnxYFWDIbh" +
                    "o5lTrnfYfV+mnWFAVQkup2NOMctHdZ22N9hIePc3oHy2mNtG16u5sc7mhNPxcfHU" +
                    "yxCz3CHoM1fzomNEE0onh0xhiB6jAgMBAAECgYAWbjdXZYvhh/hX9Frxvkv02h/u" +
                    "WD5umtlzEURMOQ2+sYiscdhZKNaNLFUx9Z4QhibfXWWWZL4cAQoOgV89Zk4IUzGp" +
                    "CyNQcI9GSGdvOHnSm9gR3VmqlAEFSnx13Xy9cKQ6kUyEasYBiV5ed+moCg41E2qh" +
                    "HwCVp+5vAdQRebnCgQJBAMdfqbD4nVNOAjWrx8LOpRDDpNiOsj15oV6BFuBV03a1" +
                    "SJlHh2vMBRPKZvcScF7+GH0wfgtcI87E/+GwQdKKA8kCQQDAm2figzwyaoghYCL3" +
                    "MYpgNlDp5faqWtWm6/rJ3S5Va3I83DuWCPGSohz82xvzUdWMdKBlMxvrEEA8JYgB" +
                    "As0LAkB8bVqVJQPiyqucXWHlVVw1EzTdJmlc7nzkYwIo7cd0rXzqlHaSsxh0Gnya" +
                    "M/rHfiNAdjQj2NRMJixf6MWbpSDpAkEAh1kt6YlbdfpS61FJgPg5S18gAR3u9sua" +
                    "NBDETbK2aqR0xLvmy4pBaE+vmyjGPXiWXntec3808TycRygAqOU0/QJBALU2B0qy" +
                    "XQ3N4pc/iurUbfeSZ8j+gXyyHYo5a95R7971Qs8hEsgd4O7DGjB2X9WR3kQmkYy7" +
                    "f6AxvE3F+yB0024=";

    /**
     * 公钥 - 仅调试使用
     */
    private static final String PUBLIC_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWAMcYXotdg7TD78Z7HxPvcMPT" +
                    "fe4058KBbegcWh8hoHrTwxpo8FGouMzBYS0XTo+BDUuKZ8WBVgyG4aOZU6532H1f" +
                    "pp1hQFUJLqdjTjHLR3WdtjfYSHj3N6B8tpjbRterubHO5oTT8XHx1MsQs9wh6DNX" +
                    "86JjRBNKJ4dMYYgeowIDAQAB";

    /**
     * 签名函数
     *
     * @param parameters 参数列表
     * @return 签名字符串
     * @throws Exception 异常
     */
    public static String signature(TreeMap<String, String> parameters, String privateKey) throws Exception {
        String charset = "utf-8";
        String toSignString = "";
        try {
            //拼接签名字符串
            StringBuilder sb = new StringBuilder();
            Set<Map.Entry<String, String>> es = parameters.entrySet();
            for (Map.Entry<String, String> entry : es) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (null != v && !"".equals(v) && !"{}".equals(v) && !FIELD_SIGN.equals(k)) {
                    sb.append(k).append("=").append(v).append("&");
                }
            }
            toSignString = sb.toString().substring(0, sb.toString().length() - 1);
            PrivateKey priKey = getPrivateKeyFromPKCS8(privateKey);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            if (StringUtils.isEmpty(charset)) {
                signature.update(toSignString.getBytes());
            } else {
                signature.update(toSignString.getBytes(charset));
            }
            byte[] signed = signature.sign();
            return BaseEncoding.base64().encode(signed);
        } catch (Exception ex) {
            throw new SignatureException("RSA content = " + toSignString + "; charset = " + charset, ex);
        }
    }

    /**
     * 签名函数
     *
     * @param parameters 参数列表
     * @return 签名字符串
     * @throws Exception 异常
     * @Author katrina
     * @date 2020-11-04
     */
    public static String signatureDaMai(TreeMap<String, String> parameters, String privateKey) throws Exception {
        String charset = "utf-8";
        String toSignString = "";
        try {
            //拼接签名字符串
            StringBuilder sb = new StringBuilder();
            Set<Map.Entry<String, String>> es = parameters.entrySet();
            for (Map.Entry<String, String> entry : es) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (null != v && !"".equals(v) && !"{}".equals(v) && !FIELD_SIGN.equals(k)) {
                    sb.append(k).append("=").append(v).append("&");
                }
            }
            toSignString = DigestUtils.md5DigestAsHex(sb.append("key=" + privateKey).toString().getBytes()).toUpperCase();
            return toSignString;
        } catch (Exception ex) {
            throw new SignatureException("RSA content = " + toSignString + "; charset = " + charset, ex);
        }
    }

    /**
     * 签名验证 - 验签错误后，日志输出正确sign，方便调试使用
     *
     * @param parameters 参数列表
     * @return boolean
     */
    public static boolean verify(TreeMap<String, String> parameters, String publicKey, String privateKey) {
        final String charset = "utf-8";
        try {
            if (parameters == null) {
                throw new Exception("参数对象不能为空.");
            }
            if (parameters.get(FIELD_SIGN) == null || StringUtils.isEmpty(parameters.get(FIELD_SIGN))) {
                throw new Exception("签名参数不能为空.");
            }
            if (parameters.get(FIELD_SIGN).length() < FIELD_SIGN_MAX_LENGTH) {
                throw new Exception("签名参数长度错误.");
            }
            //拼接签名字符串
            String toSignString;
            StringBuilder sb = new StringBuilder();
            Set<Map.Entry<String, String>> es = parameters.entrySet();
            for (Map.Entry<String, String> entry : es) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (null != v && !"".equals(v) && !FIELD_SIGN.equals(k)) {
                    sb.append(k).append("=").append(v.replaceAll("“", "\"")).append("&");
                }
            }
            toSignString = sb.toString().substring(0, sb.toString().length() - 1);
            PublicKey pubKey = getPublicKeyFromX509(publicKey);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(getContentBytes(toSignString, charset));
            if (signature.verify(BaseEncoding.base64().decode(parameters.get(FIELD_SIGN)))) {
                return true;
            } else {
                String sign = "";
                if (!StringUtils.isEmpty(privateKey)) {
                    sign = RsaSignUtil.signature(parameters, privateKey);
                }
                log.error("sign_verify_failure. sign: {}, post_sign: {}", sign, parameters.get(FIELD_SIGN));
                return false;
            }
        } catch (Exception ex) {
            log.error("sign_verify_exception", ex);
            return false;
        }
    }


    /**
     * 获取私钥
     *
     * @param privateKey 输⼊入流
     * @return PrivateKey
     * @throws Exception ex
     */
    private static PrivateKey getPrivateKeyFromPKCS8(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(SIGN_TYPE_RSA);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(BaseEncoding.base64().decode(privateKey)));
    }

    /**
     * 获取公钥
     *
     * @param publicKey 输入流
     * @return PublicKey
     * @throws NoSuchAlgorithmException ex
     */
    private static PublicKey getPublicKeyFromX509(String publicKey) throws NoSuchAlgorithmException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(SIGN_TYPE_RSA);
            return keyFactory.generatePublic(new X509EncodedKeySpec(BaseEncoding.base64().decode(publicKey)));
        } catch (Exception ex) {
            log.error("----->RSAUtil.getPublicKeyFromX509 异常信息:", ex.getMessage());
        }
        return null;
    }

    /**
     * 获取内容的二进制流
     *
     * @param content 内容
     * @param charset 字符集
     * @return byte
     */
    private static byte[] getContentBytes(String content, String charset) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(charset)) {
            return content.getBytes();
        }
        return content.getBytes(charset);
    }
}
