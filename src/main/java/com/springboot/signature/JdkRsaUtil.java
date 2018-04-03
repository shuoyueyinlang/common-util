package com.springboot.signature;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * jdk自带rsa加解密，签名工具
 *
 * @author 林锋
 * @email 904303298@qq.com
 * @create 2018-04-03 15:18
 **/
public class JdkRsaUtil {
    /**
     * 配置常量
     */
    interface Constants {
        /**
         * 最大加密长度块
         */
        int MAX_DECRYPT_BLOCK = 128;

        /**
         * 最大明文长度块
         */
        int MAX_ENCRYPT_BLOCK = 117;

        /**
         * 加密算法RSA
         */
        String KEY_ALGORITHM = "RSA";

        /**
         * 签名算法
         */
        String SIGNATURE_ALGORITHM = "MD5withRSA";

        /**
         * 获取公钥的key
         */
        String PUBLIC_KEY = "RSAPublicKey";

        /**
         * 获取私钥的key
         */
        String PRIVATE_KEY = "RSAPrivateKey";
    }

    /**
     * 使用PKCS8私钥规范加密
     *
     * @param context
     * @param privateKey
     * @return
     */
    public static String encryptByPrivateKey(String context, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(Constants.KEY_ALGORITHM);
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        return Base64.encode(encryptByPrivateKey(context.getBytes(), key));
    }

    /**
     * 使用X509公钥规范解密
     *
     * @param context
     * @param publicKey
     * @return
     */
    public static String decryptByPublicKey(String context, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(Constants.KEY_ALGORITHM);
        PublicKey key = keyFactory.generatePublic(keySpec);
        return new String(decryptByPublicKey(Base64.decode(context), key));
    }

    /**
     * 使用PKCS8私钥规范解密
     *
     * @param context
     * @param privateKey
     * @return
     */
    public static String decryptByPrivateKey(String context, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(Constants.KEY_ALGORITHM);
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        return new String(decryptByPrivateKey(Base64.decode(context), key));
    }

    /**
     * 使用X509公钥规范加密
     *
     * @param context
     * @param publicKey
     * @return
     */
    public static String encryptByPublicKey(String context, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(Constants.KEY_ALGORITHM);
        PublicKey key = keyFactory.generatePublic(keySpec);
        return Base64.encode(encryptByPublicKey(context.getBytes(), key));
    }


    /**
     * 私钥加密
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(Constants.KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return doFinal(cipher, content, Constants.MAX_ENCRYPT_BLOCK);
    }

    /**
     * 公钥解密
     *
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(Constants.KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return doFinal(cipher, content, Constants.MAX_DECRYPT_BLOCK);
    }

    /**
     * 公钥加密
     *
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(Constants.KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return doFinal(cipher, content, Constants.MAX_ENCRYPT_BLOCK);
    }

    /**
     * 私钥解密
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(Constants.KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return doFinal(cipher, content, Constants.MAX_DECRYPT_BLOCK);
    }

    /**
     * 分块处理数据
     *
     * @param cipher
     * @param content
     * @return
     * @throws Exception
     */
    private static byte[] doFinal(Cipher cipher, byte[] content, int block) throws Exception {
        int inputLen = content.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 数据分段处理
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > block) {
                cache = cipher.doFinal(content, offSet, block);
            } else {
                cache = cipher.doFinal(content, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * block;
        }
        byte[] data = out.toByteArray();
        out.close();
        return data;
    }

    public static void main(String[] args) throws Exception {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(Constants.KEY_ALGORITHM);
        keyPairGenerator.initialize(1024);
        KeyPair key = keyPairGenerator.generateKeyPair();

//        //私钥加密
//        String encryptedStr = encryptByPrivateKey("测试", Base64.encode(key.getPrivate().getEncoded()));
//        System.out.println("加密后：" + encryptedStr);
//
//        //公钥解密
//        String decryptedStr = decryptByPublicKey(encryptedStr, Base64.encode(key.getPublic().getEncoded()));
//        System.out.println("解密后：" + decryptedStr);


//        //公钥加密
//        String encryptedStr = encryptByPublicKey("测试", Base64.encode(key.getPublic().getEncoded()));
//        System.out.println("加密后：" + encryptedStr);
//
//        //私钥解密
//        String decryptedStr = decryptByPrivateKey(encryptedStr, Base64.encode(key.getPrivate().getEncoded()));
//        System.out.println("解密后：" + decryptedStr);
    }
}
