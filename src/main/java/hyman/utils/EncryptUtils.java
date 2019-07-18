package hyman.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {

    /**
     * Logger对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtils.class);

    /**
     *
     * <p><b>方法描述：</b>进行MD5加密</p>
     * @param info 要加密的信息
     * @return String 加密后的字符串
     */
    public static String encryptToMD5(String info) {
        byte[] digesta = null;
        try {
            // 得到一个md5的消息摘要
            MessageDigest alga = MessageDigest.getInstance("MD5");
            // 添加要进行计算摘要的信息
            alga.update(info.getBytes());
            // 得到该摘要
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
            return null;
        }
        // 将摘要转为字符串
        String rs = byte2hex(digesta);
        return rs;
    }

    /**
     *
     * <p><b>方法描述：</b>进行SHA加密</p>
     * @param info 要加密的信息
     * @return String 加密后的字符串
     */
    public static String encryptToSHA(String info) {
        byte[] digesta = null;
        try {
            // 得到一个SHA-1的消息摘要
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            // 添加要进行计算摘要的信息
            alga.update(info.getBytes());
            // 得到该摘要
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
            return null;
        }
        // 将摘要转为字符串
        String rs = byte2hex(digesta);
        return rs;
    }

    /**
     *
     * <p><b>方法描述：</b>根据一定的算法得到相应的key</p>
     * @param algorithm 加密算法,可用 AES,DES
     * @param src 要加密的字符串
     * @return String 相应的key
     */
    public static String getKey(String algorithm, String src) {
        if (algorithm.equals("AES")) {
            return src.substring(0, 16);
        } else if (algorithm.equals("DES")) {
            return src.substring(0, 8);
        } else {
            return null;
        }
    }

    /**
     *
     * <p><b>方法描述：</b>得到AES加密的key</p>
     * @param src 要加密的字符串
     * @return String AES加密的key
     */
    public static String getAESKey(String src) {
        return getKey("AES", src);
    }

    /**
     *
     * <p><b>方法描述：</b>得到DES加密的key</p>
     * @param src 要加密的字符串
     * @return String DES加密的key
     */
    public static String getDESKey(String src) {
        return getKey("DES", src);
    }

    /**
     *
     * <p><b>方法描述：</b>创建密匙</p>
     * @param algorithm 加密算法,可用 AES,DES,DESede,Blowfish
     * @return SecretKey 秘密（对称）密钥
     */
    public static SecretKey createSecretKey(String algorithm) {
        // 声明KeyGenerator对象
        KeyGenerator keygen;
        // 声明 密钥对象
        SecretKey deskey = null;
        try {
            // 返回生成指定算法的秘密密钥的 KeyGenerator 对象
            keygen = KeyGenerator.getInstance(algorithm);
            // 生成一个密钥
            deskey = keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        }
        // 返回密匙
        return deskey;
    }

    /**
     *
     * <p><b>方法描述：</b>创建一个AES的密钥</p>
     * @return SecretKey AES密钥
     */
    public static SecretKey createSecretAESKey() {
        return createSecretKey("AES");
    }

    /**
     *
     * <p><b>方法描述：</b>创建一个DES的密钥</p>
     * @return SecretKey DES的密钥
     */
    public static SecretKey createSecretDESKey() {
        return createSecretKey("DES");
    }

    /**
     *
     * <p><b>方法描述：</b>根据相应的加密算法、密钥、源文件进行加密，返回加密后的字符串</p>
     * @param algorithm 加密算法,可用 AES,DES,DESede,Blowfish
     * @param key 密钥
     * @param info 要加密的信息
     * @return String 加密后的字符串
     */
    public static String encrypt(String algorithm, SecretKey key, String info) {
        // 定义要生成的密文
        byte[] cipherByte = null;
        // 得到加密/解密器
        Cipher c1;
        try {
            c1 = Cipher.getInstance(algorithm);
            // 用指定的密钥和模式初始化Cipher对象
            // 参数:(ENCRYPT_MODE, DECRYPT_MODE, WRAP_MODE,UNWRAP_MODE)
            c1.init(Cipher.ENCRYPT_MODE, key);
            // 对要加密的内容进行编码处理,
            cipherByte = c1.doFinal(info.getBytes());
        } catch (NoSuchAlgorithmException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (NoSuchPaddingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (InvalidKeyException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (IllegalBlockSizeException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (BadPaddingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        }
        // 返回密文的十六进制形式
        return byte2hex(cipherByte);
    }

    /**
     *
     * <p><b>方法描述：</b>根据相应的解密算法、密钥和需要解密的文本进行解密，返回解密后的文本内容</p>
     * @param algorithm 加密算法,可用 AES,DES,DESede,Blowfish
     * @param key 密钥
     * @param info 要加密的信息
     * @return String 解密后的文本内容
     */
    public static String decrypt(String algorithm, SecretKey key, String info) {
        byte[] cipherByte = null;
        // 得到加密/解密器
        Cipher c1;
        try {
            c1 = Cipher.getInstance(algorithm);
            // 用指定的密钥和模式初始化Cipher对象
            c1.init(Cipher.DECRYPT_MODE, key);
            // 对要解密的内容进行编码处理
            cipherByte = c1.doFinal(hex2byte(info));
        } catch (NoSuchAlgorithmException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (NoSuchPaddingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (InvalidKeyException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (IllegalBlockSizeException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (BadPaddingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        }
        return new String(cipherByte);
    }

    /**
     *
     * <p><b>方法描述：</b>根据相应的解密算法、指定的密钥和需要解密的文本进行解密，返回解密后的文本内容</p>
     * @param algorithm 加密算法,可用 AES,DES,DESede,Blowfish
     * @param src 加密字符串
     * @param key 密钥
     * @return String 解密后的文本内容
     */
    public static String decrypt(String algorithm, String src, String key) {
        // 判断Key是否正确
        if (key == null) {
            throw new IllegalArgumentException("Key为空null");
        }
        // 判断采用AES加解密方式的Key是否为16位
        if (algorithm.equals("AES") && key.length() != 16) {
            throw new IllegalArgumentException("Key长度不是16位");
        }
        // 判断采用DES加解密方式的Key是否为8位
        if (algorithm.equals("DES") && key.length() != 8) {
            throw new IllegalArgumentException("Key长度不是8位");
        }
        byte[] raw;
        try {
            raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = hex2byte(src);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        } catch (UnsupportedEncodingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (NoSuchAlgorithmException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (NoSuchPaddingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (InvalidKeyException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (IllegalBlockSizeException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (BadPaddingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        }
        return "";
    }

    /**
     *
     * <p><b>方法描述：</b>根据相应的加密算法、指定的密钥、源文件进行加密，返回加密后的文本内容</p>
     * @param algorithm 加密算法,可用 AES,DES,DESede,Blowfish
     * @param src 加密字符串
     * @param key 密钥
     * @return String 加密后的文本内容
     */
    public static String encrypt(String algorithm, String src, String key) {
        // 判断Key是否正确
        if (key == null) {
            throw new IllegalArgumentException("Key为空null");
        }
        // 判断采用AES加解密方式的Key是否为16位
        if (algorithm.equals("AES") && key.length() != 16) {
            throw new IllegalArgumentException("Key长度不是16位");
        }
        // 判断采用DES加解密方式的Key是否为8位
        if (algorithm.equals("DES") && key.length() != 8) {
            throw new IllegalArgumentException("Key长度不是8位");
        }
        byte[] raw;
        try {
            raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(src.getBytes());
            return byte2hex(encrypted);
        } catch (UnsupportedEncodingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (NoSuchAlgorithmException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (NoSuchPaddingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (InvalidKeyException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (IllegalBlockSizeException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (BadPaddingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        }
        return "";
    }

    /**
     *
     * <p><b>方法描述：</b>采用DES随机生成的密钥进行加密</p>
     * @param key 密钥
     * @param info 要加密的信息
     * @return String 加密后的文本信息
     */
    public static String encryptToDES(SecretKey key, String info) {
        return encrypt("DES", key, info);
    }

    /**
     *
     * <p><b>方法描述：</b>采用DES指定密钥的方式进行加密</p>
     * @param key 密钥
     * @param info 要加密的信息
     * @return String 加密后的文本信息
     */
    public static String encryptToDES(String key, String info) {
        return encrypt("DES", info, key);
    }

    /**
     *
     * <p><b>方法描述：</b>采用DES随机生成密钥的方式进行解密，密钥需要与加密的生成的密钥一样</p>
     * @param key 密钥
     * @param info 要加密的信息
     * @return String 加密后的文本信息
     */
    public static String decryptByDES(SecretKey key, String info) {
        return decrypt("DES", key, info);
    }

    /**
     *
     * <p><b>方法描述：</b>采用DES用户指定密钥的方式进行解密，密钥需要与加密时指定的密钥一样</p>
     * @param key 密钥
     * @param info 要加密的文本信息
     * @return String 加密后的文本信息
     */
    public static String decryptByDES(String key, String info) {
        return decrypt("DES", info, key);
    }

    /**
     *
     * <p><b>方法描述：</b>采用AES随机生成的密钥进行加密</p>
     * @param key 密钥
     * @param info 要加密的文本信息
     * @return String 加密后的文本信息
     */
    public static String encryptToAES(SecretKey key, String info) {
        return encrypt("AES", key, info);
    }

    /**
     *
     * <p><b>方法描述：</b>采用AES指定密钥的方式进行加密</p>
     * @param key 密钥
     * @param info 要加密的信息
     * @return String 加密后的文本信息
     */
    public static String encryptToAES(String key, String info) {
        return encrypt("AES", info, key);
    }

    /**
     *
     * <p><b>方法描述：</b>采用AES随机生成密钥的方式进行解密，密钥需要与加密的生成的密钥一样</p>
     * @param key 密钥
     * @param info 加密字符串
     * @return String 解密后的文本信息
     */
    public static String decryptByAES(SecretKey key, String info) {
        return decrypt("AES", key, info);
    }

    /**
     *
     * <p><b>方法描述：</b>采用AES用户指定密钥的方式进行解密，密钥需要与加密时指定的密钥一样</p>
     * @param key 密钥
     * @param info 加密字符串
     * @return String 解密后的文本信息
     */
    public static String decryptByAES(String key, String info) {
        return decrypt("AES", info, key);
    }

    /**
     *
     * <p><b>方法描述：</b>十六进制字符串转化为2进制</p>
     * @param strhex 16进制字符串
     * @return byte[]
     */
    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    /**
     *
     * <p><b>方法描述：</b>将二进制转化为16进制字符串</p>
     * @param b 二进制字节数组
     * @return String 16进制字符串
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }
}
