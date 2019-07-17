package hyman.utils;

import com.jagregory.shiro.freemarker.ShiroTags;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class Constant {

    static {
        init("/config/sys.properties");
    }

    public static final String STATIC_PATH = getProperty("StaticPath", "");

    public static final String ACCESS_PATH = getProperty("FilePath", "") + "showimg/";

    public static final String JS_PATH = getProperty("js.path", "");

    public static final String ROOT_PATH = getProperty("root.path", "");

    public static final String FILE_PATH = getProperty("FilePath", "");

    public static final String PUT_PATH = getProperty("PutPath", "");

    public static final String UPLOAD_PATH = getProperty("FilePath", "") + "upload/upload-file.json";

    public static final String DOWNLOAD_PATH = getProperty("FilePath", "") + "upload/upload-download?fileid=";

    public static final String PAGE_KEY_WORDS = getProperty("page.key.words", "");

    public static final String IMAGE_TYPE = getProperty("image.type", "");

    public static final String IMAGE_MAXSIZE = getProperty("image.maxsize", "");

    public static final String DOC_TYPE = getProperty("doc.type", "");

    public static final String SEND_MSG_URL = getProperty("send.msg.url", "");

    public static final String SEND_EMAIL_URL = getProperty("send.email.url", "");

    public static final String PROXY_IP_NAME = getProperty("proxy.ip.name", "");

    public static final String IM_PATH = getProperty("im.path", "");

    private static Properties p;

    private Constant() {

    }

    protected static void init(String propertyFileName) {
        InputStream in = null;
        p = new Properties();
        try {
            in = Constant.class.getResourceAsStream(propertyFileName);
            p.clear();
            if (in != null) {
                p.load(in);
            }

        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String getProperty(String key, String defaultValue) {
        return p.getProperty(key, defaultValue);
    }
}