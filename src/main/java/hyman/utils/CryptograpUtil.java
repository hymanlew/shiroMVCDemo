package hyman.utils;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.Md5Hash;

// 加密解密工具类
public class CryptograpUtil {

    // Base64 编码加密
    public static String enBase64(String str){
        return Base64.encodeToString(str.getBytes());
    }

    // Base64 编码解密
    public static String decBase64(String str){
        return Base64.decodeToString(str);
    }

    // 16 进制字符串编码 / 解码
    public static void codeBy16(String str){
        String codes = Hex.encodeToString(str.getBytes());
        System.out.println(codes);

        String decodes = new String(Hex.decode(codes.getBytes()));
        System.out.println(decodes);
    }

    // md5 加密，salt 加盐，这种方式加密是不可逆的。即不可破解
    public static String md5(String str,String salt){
        // 还可以转换为 toBase64() / toHex()
        // 另外在进行散列计算时还可以指定散列次数，如 2 次表示：md5(md5(str))：“new Md5Hash(str, salt, 2).toString()”
        return new Md5Hash(str,salt).toString();
    }

    public static void main(String[] args) {
        String password = "123456";
        String s = CryptograpUtil.enBase64(password);
        System.out.println(s);
        System.out.println(CryptograpUtil.decBase64(s));

        System.out.println(CryptograpUtil.md5("teacher","hyman"));
    }

    /**
     * Shiro 编码加密：
     * 在涉及到密码存储问题上，应该加密/生成密码摘要存储，而不是存储明文密码。Shiro 提供了 base64 和 16 进制字符串编码/解码
     * 的 API 支持，方便一些编码解码操作。Shiro 内部的一些数据的存储/表示都使用了 base64 和 16 进制字符串。
     *
     * 还有一个可能经常用到的类 CodecSupport，提供了 toBytes(str,"utf-8") / toString(bytes,"utf-8") 用于在 byte 数组 /
     * String 之间转换。
     *
     * 散列算法：一般用于生成数据的摘要信息，是一种不可逆的算法，一般适合存储密码之类的数据。
     * 常见的散列算法如 MD5、SHA 等。一般进行散列时最好提供一个 salt（盐）， 因为如果直接对密码进行散列计算，相对来说破解更
     * 容易，可以到一些 md5 解密网站很容易通过散列值得到密码。此时我们可以加一些只有系统知道的干扰数据，如用户名和 ID（即盐）
     * ；这样散列的对象是 “密码 + 用户名 +ID”，这样生成的散列值相对来说更难破解。
     *
     *
     * String sha1 = new Sha256Hash(str, salt).toString();，使用 SHA256 算法生成相应的散列数据，另外还有如 SHA1、SHA512 算法。
     *
     * Shiro 还提供了通用的散列支持（内部使用MessageDigest）：
     * String simpleHash = new SimpleHash("SHA-1", str, salt).toString();，通过调用 SimpleHash 时指定散列算法，其内部使
     * 用了 Java 的 MessageDigest 实现。
     */

    /**
     *
     为了方便使用，Shiro 提供了 HashService，默认提供了 DefaultHashService 实现。

     DefaultHashService hashService = new DefaultHashService(); //默认算法SHA-512
     hashService.setHashAlgorithmName("SHA-512");
     hashService.setPrivateSalt(new SimpleByteSource("123")); //私盐，默认无
     hashService.setGeneratePublicSalt(true);//是否生成公盐，默认false
     hashService.setRandomNumberGenerator(new SecureRandomNumberGenerator());//用于生成公盐。默认就这个
     hashService.setHashIterations(1); //生成Hash值的迭代次数
     HashRequest request = new HashRequest.Builder()
     .setAlgorithmName("MD5").setSource(ByteSource.Util.bytes("hello"))
     .setSalt(ByteSource.Util.bytes("123")).setIterations(2).build();
     String hex = hashService.computeHash(request).toHex();

     首先创建一个 DefaultHashService，默认使用 SHA-512 算法；
     以通过 hashAlgorithmName 属性修改算法；
     可以通过 privateSalt 设置一个私盐，其在散列时自动与用户传入的公盐混合产生一个新盐；
     可以通过 generatePublicSalt 属性在用户没有传入公盐的情况下是否生成公盐；
     可以设置 randomNumberGenerator 用于生成公盐；
     可以设置 hashIterations 属性来修改默认加密迭代次数；
     需要构建一个 HashRequest，传入算法、数据、公盐、迭代次数。

     SecureRandomNumberGenerator 用于生成一个随机数：
     SecureRandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
     randomNumberGenerator.setSeed("123".getBytes());
     String hex = randomNumberGenerator.nextBytes().toHex();


     Shiro 还提供对称式加密 / 解密算法的支持，如 AES、Blowfish 等；当前还没有提供对非对称加密 / 解密算法支
     持，未来版本可能提供。
     AES 算法实现：

     AesCipherService aesCipherService = new AesCipherService();
     aesCipherService.setKeySize(128); //设置key长度
     //生成key
     Key key = aesCipherService.generateNewKey();
     String text = "hello";
     //加密
     String encrptText =
     aesCipherService.encrypt(text.getBytes(), key.getEncoded()).toHex();
     //解密
     String text2 =
     new String(aesCipherService.decrypt(Hex.decode(encrptText), key.getEncoded()).getBytes());
     Assert.assertEquals(text, text2);

     */
}
