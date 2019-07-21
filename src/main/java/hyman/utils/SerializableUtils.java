package hyman.utils;

import org.apache.shiro.session.Session;

import java.io.*;
import java.util.Base64;

/**
 * 自定义序列化，反序列化工具类。即相当于重写了 spring 的工具类 org.springframework.util.SerializationUtils;
 *
 * Base64 与 UrlEncode 区别：
 * base64：
 * 1、包含A-Z a-z 0-9 和加号“+”，斜杠“/” 用来作为开始的64个数字. 等号“=”用来作为后缀用途。
 * 2、2进制的.
 * 3、要比源数据多33%。
 * 4、= 号的个数是由 /3 的余数来决定的，最多能有 2 个 = 号；
 * 5、常用于邮件。主要用于初步的加密（非明文可见）和安全的网络传输，即程序内部使用的。
 *
 * urlencode：
 * 1、它只能使用 ASCII 字符集来通过因特网进行发送。
 * 2、除了  -_.  等规定之外的所有非字母数字字符都将被替换成百分号（%）后跟两位十六进制数，空格则编码为加号（+）
 * 3、主要用于编码 url 和安全传输 url。
 *
 * RFC 1738做了硬性规定："只有字母和数字[0-9a-zA-Z]、一些特殊符号"$-_.+!*'(),"[不包括双引号]、以及某些保留字，才可以不经过
 * 编码直接用于URL。"
 * 所以 htttp 传输必须使用 urlencode。
 */
public class SerializableUtils {

    /**
     * <p><b>方法描述：</b>序列化方法</p>
     * @param object object对象
     * @return byte[]
     */
    public static byte[] serialize(Object object) {
        byte[] result = null;
        if (object == null) {
            return new byte[0];
        }
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
            try {
                if (!(object instanceof Serializable)) {
                    throw new IllegalArgumentException(
                            SerializableUtils.class.getSimpleName() + " requires a Serializable payload "
                                    + "but received an object of type [" + object.getClass().getName() + "]");
                }
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
                result = byteStream.toByteArray();
            } catch (Throwable ex) {
                throw new Exception("Failed to serialize", ex);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * <p><b>方法描述：</b>反序列化方法</p>
     * @param bytes byte[]
     * @return Object
     */
    public static Object deserialize(byte[] bytes) {
        Object result = null;
        if (isEmpty(bytes)) {
            return null;
        }
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
                try {
                    result = objectInputStream.readObject();
                } catch (ClassNotFoundException ex) {
                    throw new Exception("Failed to deserialize object type", ex);
                }
            } catch (Throwable ex) {
                throw new Exception("Failed to deserialize", ex);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * <p><b>方法描述：</b>判断是否为空</p>
     * @param data byte[]
     * @return true or false
     */
    public static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    public static String serializ(Session session){
        try {
            // 字节数组输出流
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // 对象输出流
            ObjectOutputStream out = new ObjectOutputStream(bos);
            // 将 Object 对象输出成 byte 数据
            out.writeObject(session);

            // 将字节数组输出流转换成字节数组，并编码成 String 类型数据
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("session 序列化失败！");
        }
    }

    public static Session deserializ(String str){
        try {
            byte[] bytes = Base64.getDecoder().decode(str);
            // 字节数组输入流
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bis);

            //将字节码反序列化成 对象
            Session session = (Session) in.readObject();
            return session;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("session 反序列化失败！");
        }
    }
}
