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
        init("/config/config.properties");
    }

    /**
     * 闈欐�佽祫婧愯矾寰�
     */
    public static final String STATIC_PATH = getProperty("StaticPath", "");

    /**
     * 鍥剧墖璁块棶璺緞
     */
    public static final String ACCESS_PATH = getProperty("FilePath", "") + "showimg/";

    /**
     * js璧勬簮璺緞
     */
    public static final String JS_PATH = getProperty("js.path", "");

    /**
     * 褰撳墠椤圭洰璺緞
     */
    public static final String ROOT_PATH = getProperty("root.path", "");

    /**
     * 涓婁紶鏂囦欢鏈嶅姟鍣ㄥ湴鍧�
     */
    public static final String FILE_PATH = getProperty("FilePath", "");

    /**
     * 鏂囦欢涓婁紶鍦板潃
     */
    public static final String UPLOAD_PATH = getProperty("FilePath", "") + "upload/upload-file.json";

    /**
     * 鏂囦欢涓嬭浇鍦板潃
     */
    public static final String DOWNLOAD_PATH = getProperty("FilePath", "") + "upload/upload-download?fileid=";

    /**
     * 鏂囦欢鍓垏鍦板潃
     */
    public static final String CUT_PATH = getProperty("FilePath", "") + "upload/cut-image/";

    /**
     * 椤甸潰鍏抽敭璇�
     */
    public static final String PAGE_KEY_WORDS = getProperty("page.key.words", "");

    /**
     * 椤甸潰鏍囬
     */
    public static final String PAGE_TITLE = getProperty("page.title", "");

    /**
     * 椤甸潰鎻忚堪
     */
    public static final String PAGE_DESC = getProperty("page.desc", "");

    /**
     * 椤甸潰缂撳瓨
     */
    public static final String PAGE_CACHE = getProperty("page.cache", "");

    /**
     * 鏂囦欢涓婁紶绫诲瀷
     */
    public static final String IMAGE_TYPE = getProperty("image.type", "");

    /**
     * 鏈�澶ф枃浠朵笂浼犲ぇ灏�
     */
    public static final String IMAGE_MAXSIZE = getProperty("image.maxsize", "");

    /**
     * office鏂囦欢绫诲瀷
     */
    public static final String DOC_TYPE = getProperty("doc.type", "");

    /**
     * 鐭俊鍙戦�佸湴鍧�
     */
    public static final String SEND_MSG_URL = getProperty("send.msg.url", "");

    /**
     * 鐭俊鍙戦�佸湴鍧�
     */
    public static final String SEND_EMAIL_URL = getProperty("send.email.url", "");

    /**
     * 鏄惁浣跨敤nginx,绌哄垯涓嶄娇鐢紝涓嶄负绌哄垯涓鸿幏鍙朓P鐨勬爣璇�
     */
    public static final String PROXY_IP_NAME = getProperty("proxy.ip.name", "");

    /**
     * 椤圭洰鍚嶇О
     */
    public static final String APP_NAME = getProperty("app.name", "");

    /**
     * 椤圭洰缂栫爜
     */
    public static final String APP_CODE = getProperty("app.code", "");

    /**
     * 鍞竴璇嗗埆鐮佸伐浣淚D
     */
    public static final String UUID_WORKERID = getProperty("uuid.workerid", "");

    /**
     * 鍞竴璇嗗埆鐮佹暟鎹腑蹇僆D
     */
    public static final String UUID_DATACENTERID = getProperty("uuid.datacenterid", "");

    /**
     * im璺緞
     */
    public static final String IM_PATH = getProperty("im.path", "");

    /**
     * Properties瀵硅薄
     */
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