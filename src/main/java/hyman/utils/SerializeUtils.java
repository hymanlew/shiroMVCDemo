package hyman.utils;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

    public class SerializeUtils {

    /**
     * Schema缓存
     */
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    /**
     *
     * <p><b>方法描述：</b>反序列化方法</p>
     * @param bytes byte[]
     * @deprecated
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
     * @deprecated
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

    /**
     *
     * <p><b>方法描述：</b>获取Schema</p>
     * @param <T> 类泛型
     * @param cls clss对象
     * @return Schema对象
     */
    private static <T> Schema<T> getSchema(Class<T> cls) {
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Scheme<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }

    /**
     *
     * <p><b>方法描述：</b>序列化方法</p>
     * @param <T> 类泛型
     * @param obj 要序列化的类对象
     * @return 序列后的字节数组
     */
    public static <T> byte[] protoSerialize(T obj) {
        @SuppressWarnings("unchecked")
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     *
     * <p><b>方法描述：</b>反序列化方法</p>
     * @param <T> 类泛型
     * @param data 序列化数据
     * @param cls 类class对象
     * @return 类对象
     */
    public static <T> T protoDeserialize(byte[] data, Class<T> cls) {
        try {
            Schema<T> schema = getSchema(cls);
            T message = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
