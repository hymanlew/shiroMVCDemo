package hyman.config.redis;

import org.apache.shiro.util.SimpleByteSource;

import java.io.Serializable;

public class RedisSimpleByteSource extends SimpleByteSource implements Serializable {

    private static final long serialVersionUID = 2232815662715161922L;

    public RedisSimpleByteSource(byte[] bytes) {
        super(bytes);
    }
    public RedisSimpleByteSource(String string) {
        super(string);
    }
}
