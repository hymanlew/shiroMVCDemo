package hyman.security.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES加密工具类
 */
public class AESSecretUtil {

    /**秘钥的大小*/
    private static final int KEYSIZE = 128;

    /**
     * AES加密
     * @param data - 待加密内容
     * @param key - 加密秘钥
     */
    public static byte[] encrypt(String data, String key) {

        if(StringUtils.isNotBlank(data)){

            try {
                SecretKeySpec secretKeySpec = getSecretKeySpec(key);

                // 创建密码器
                Cipher cipher = Cipher.getInstance("AES");
                // 初始化
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                // 加密
                byte[] byteContent = data.getBytes("UTF-8");
                return cipher.doFinal(byteContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static SecretKeySpec getSecretKeySpec(String key) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        // 选择一种固定算法，为了避免不同java实现的不同算法，生成不同的密钥，而导致解密失败
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(key.getBytes());
        keyGenerator.init(KEYSIZE, random);

        SecretKey secretKey = keyGenerator.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        return new SecretKeySpec(enCodeFormat, "AES");
    }

    /**
     * AES解密
     * @param data - 待解密字节数组
     * @param key - 秘钥
     */
    public static byte[] decrypt(byte[] data, String key) {

        if (ArrayUtils.isNotEmpty(data)) {

            try {
                SecretKeySpec secretKeySpec = getSecretKeySpec(key);
                // 创建密码器
                Cipher cipher = Cipher.getInstance("AES");
                // 初始化
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                // 加密
                return cipher.doFinal(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * AES解密，返回String
     * @param enCryptdata - 待解密字节数组
     * @param key - 秘钥
     */
    public static String decryptToStr(String enCryptdata, String key) {
        return StringUtils.isNotBlank(enCryptdata)?new String(decrypt(parseHexStr2Byte(enCryptdata), key)):null;
    }

    /**
     * AES加密，返回String
     * @param data - 待加密内容
     * @param key - 加密秘钥
     */
    public static String encryptToStr(String data, String key){
        return StringUtils.isNotBlank(data)?parseByte2HexStr(encrypt(data, key)):null;
    }

    /**
     * @Description: 将二进制转换成16进制
     * @param buf - 二进制数组
     */
    public static String parseByte2HexStr(byte[] buf) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * @Description: 将16进制转换为二进制
     * @param hexStr - 16进制字符串
     */
    public static byte[] parseHexStr2Byte(String hexStr) {

        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
        String ss = encryptToStr("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIxMjMiLCJ1c2VyTmFtZSI6Ikp1ZHkiLCJleHAiOjE1MzI3Nzk2MjIsIm5iZiI6MTUzMjc3NzgyMn0.sIw_leDZwG0pJ8ty85Iecd_VXjObYutILNEwPUyeVSo", "hymanSalt");
        System.out.println(ss);
        System.out.println(decryptToStr(ss, "hymanSalt"));
    }
}
