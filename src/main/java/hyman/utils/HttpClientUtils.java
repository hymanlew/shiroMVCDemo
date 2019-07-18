package hyman.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

public class HttpClientUtils {


    /**
     * Logger对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * 连接池管理对象
     */
    private static PoolingHttpClientConnectionManager cm;
    /**
     * UTF-8编码方式
     */
    private static final String UTF_8 = "UTF-8";

    /**
     *
     * <p><b>方法描述：</b>初始化方法</p>
     */
    private static void init() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(200); // 整个连接池最大连接数
            cm.setDefaultMaxPerRoute(20); // 每路由最大连接数，默认值是2
        }
    }

    /**
     *
     * <p><b>方法描述：</b>获取HttpClient</p>
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient getHttpClient() {
        init();
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     *
     * <p><b>方法描述：</b>获取Http get请求</p>
     * @param url 请求url
     * @return String
     */
    public static String httpGetRequest(String url) {
        HttpGet httpGet = new HttpGet(url);
        return getResult(httpGet);
    }

    /**
     *
     * <p><b>方法描述：</b>获取Http post请求</p>
     * @param url 请求url
     * @return String
     */
    public static String httpPostRequest(String url) {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }

    /**
     *
     * <p><b>方法描述：</b>获取Http get请求</p>
     * @param url 请求url
     * @param params 请求参数
     * @return String
     */
    public static String httpGetRequest(String url, Map<String, Object> params) {
        HttpGet httpGet = new HttpGet(buildURI(url, params));
        return getResult(httpGet);
    }

    /**
     *
     * <p><b>方法描述：</b>获取Http post请求</p>
     * @param url 请求url
     * @param params 请求参数
     * @return String
     */
    public static String httpPostRequest(String url, Map<String, Object> params) {
        return getResult(buildHttpPost(url, params));
    }

    /**
     *
     * <p><b>方法描述：</b>httpGetRequest</p>
     * @param url 请求url
     * @param responseHandler responseHandler对象
     * @param <T> 泛型
     * @return T 泛型
     */
    public static <T> T httpGetRequest(String url, final ResponseHandler<? extends T> responseHandler) {
        HttpGet httpGet = new HttpGet(url);
        return getResult(httpGet, responseHandler);
    }

    /**
     *
     * <p><b>方法描述：</b>httpPostRequest</p>
     * @param url 请求url
     * @param responseHandler responseHandler对象
     * @param <T> 泛型
     * @return T 泛型
     */
    public static <T> T httpPostRequest(String url, final ResponseHandler<? extends T> responseHandler) {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost, responseHandler);
    }

    /**
     *
     * <p><b>方法描述：</b>httpGetRequest</p>
     * @param url 请求url
     * @param params 请求参数
     * @param responseHandler responseHandler对象
     * @param <T> 泛型
     * @return T 泛型
     */
    public static <T> T httpGetRequest(String url, Map<String, Object> params,
                                       final ResponseHandler<? extends T> responseHandler) {
        HttpGet httpGet = new HttpGet(buildURI(url, params));
        return getResult(httpGet, responseHandler);
    }

    /**
     *
     * <p><b>方法描述：</b>httpPostRequest</p>
     * @param url 请求url
     * @param params 请求参数
     * @param responseHandler responseHandler
     * @param <T> 泛型
     * @return T 泛型
     */
    public static <T> T httpPostRequest(String url, Map<String, Object> params,
                                        final ResponseHandler<? extends T> responseHandler) {
        return getResult(buildHttpPost(url, params), responseHandler);
    }

    /**
     *
     * <p><b>方法描述：</b>buildURI</p>
     * @param url 请求url
     * @param params 请求参数
     * @return URI
     */
    private static URI buildURI(String url, Map<String, Object> params) {
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);
        ArrayList<NameValuePair> pairs = covertParams(params);
        ub.setParameters(pairs);
        URI uri = null;
        try {
            uri = ub.build();
        } catch (URISyntaxException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        }
        return uri;
    }

    /**
     *
     * <p><b>方法描述：</b>buildHttpPost</p>
     * @param url 请求url
     * @param params 请求参数
     * @return HttpPost
     */
    private static HttpPost buildHttpPost(String url, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(url);
        ArrayList<NameValuePair> pairs = covertParams(params);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        } catch (UnsupportedEncodingException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        }
        return httpPost;
    }

    /**
     *
     * <p><b>方法描述：</b>covertParams</p>
     * @param params 参数
     * @return ArrayList
     */
    private static ArrayList<NameValuePair> covertParams(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

    /**
     *
     * <p><b>方法描述：</b>getResult</p>
     * @param request request对象
     * @param responseHandler responseHandler对象
     * @param <T> 泛型
     * @return <T> T 泛型
     */
    private static <T> T getResult(HttpRequestBase request, final ResponseHandler<? extends T> responseHandler) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            return httpClient.execute(request, responseHandler);
        } catch (ClientProtocolException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     *
     * <p><b>方法描述：</b>getResult</p>
     * @param request request对象
     * @return String
     */
    private static String getResult(HttpRequestBase request) {
        return getResult(request, new BasicResponseHandler());
    }
}
