package hyman.utils;

import java.io.*;

public class SerializeUtils {

    /**
     *
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
     *
     * <p><b>方法描述：</b>判断是否为空</p>
     * @param data byte[]
     * @return true or false
     */
    public static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    /**
     *
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
                            SerializeUtils.class.getSimpleName() + " requires a Serializable payload "
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

}
