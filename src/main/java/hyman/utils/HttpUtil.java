package hyman.utils;

import org.apache.commons.codec.EncoderException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by on 2017/2/21.
 */
public class HttpUtil {

    private static BasicCookieStore cookieStore = new BasicCookieStore();
    private static String JSESSIONID="DcT1hrPYmXQz33L453vCsNLLhbTgyZwTCl6wD7sJpySS6cxgr3jQ!1372965369";
    private static BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", JSESSIONID);
    private static CloseableHttpClient httpClient = createHttpsClient();

    //创建http client
    public static CloseableHttpClient createHttpsClient() {

        X509TrustManager x509mgr = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] xcs, String string) {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] xcs, String string) {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        //因为java客户端要进行安全证书的认证，这里我们设置ALLOW_ALL_HOSTNAME_VERIFIER来跳过认证，否则将报错
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509mgr}, null);
            sslsf = new SSLConnectionSocketFactory(
                    sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        cookieStore.addCookie(cookie);
        return HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    public static String getData(String url) throws IOException, EncoderException {

        HttpGet httpGet = new HttpGet(url);

        //在header里添加认证的 token，可以进行权限认证，并执行get请求获取json数据
        //httpGet.addHeader("Authorization", "admin");

        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        return body;
    }

    /**
     * json 字符串post请求
     */
    public static String getPostData(String url,String postBody) throws IOException, EncoderException {

        HttpPost httpPost = new HttpPost(url);

        /**
         * List<NameValuePair> nvps = new ArrayList<NameValuePair>();
         * nvps.add(new BasicNameValuePair("KEY1", "VALUE1"));
         * httpPost.setEntity(new UrlEncodedFormEntity(nvps));
         *
         * 使用 UrlEncodedFormEntity 来设置 body，消息体内容类似于 “KEY1=VALUE1&KEY2=VALUE2&...” 这种形式，服务端接收以后
         * 也要依据这种协议形式做处理。
         *
         * 如果是想使用 json 格式来设置 body，就可以使用 StringEntity 类。
         *
         * 其实采用 StringEntity 形式更加自由，除了json，你也可以使用其它任意的字符串，只要服务端能做相应处理即可。
         */
        StringEntity se = new StringEntity(postBody,"UTF-8");
        se.setContentType("text/json;charset=UTF-8");
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpPost.setEntity(se);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        return body;
    }

    /**
     * 参数map post请求
     */
    public static String getPostData(String url,Map<String,String> paramMap) throws IOException {

        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder=MultipartEntityBuilder.create();
        for(String key: paramMap.keySet()){
            builder.addTextBody(key,paramMap.get(key));
        }
        builder.setCharset(Charset.forName("UTF-8"));
        httpPost.setEntity(builder.build());
        System.out.println(httpPost.getURI());
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);

        return body;
    }

    /**
     * 参数map post请求
     */
    public static String getPostData(String url,Map<String,String>paramMap,Map<String,String>headerMap) throws IOException, EncoderException {

        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder=MultipartEntityBuilder.create();
        for(String key: paramMap.keySet()){
            builder.addTextBody(key,paramMap.get(key));
        }
        builder.setCharset(Charset.forName("UTF-8"));

        httpPost.setEntity(builder.build());
        if(headerMap!=null&&headerMap.size()>0){
            for(String key: headerMap.keySet()){
                httpPost.addHeader(key,headerMap.get(key));
            }
        }
        CloseableHttpResponse response = httpClient.execute(httpPost);
        List<Cookie> cookies = cookieStore.getCookies();

        for (int i = 0; i < cookies.size(); i++) {
            System.out.println(cookies.get(i).getName()+":"+cookies.get(i).getValue());
        }
        Header[]headers= response.getAllHeaders();
        for(Header header:headers){
            System.out.println(header.getName()+":"+header.getValue());
        }
//        if(response.getFirstHeader("Set-Cookie")!=null){
//            String setCookie = response.getFirstHeader("Set-Cookie").getValue();
//            JSESSIONID = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
//            System.out.println("JSESSIONID:" + JSESSIONID);
//        }
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);

        return body;
    }


    /**
     * json 字符串post请求
     */
    public static String getPostData(Map<String,String> headerMap, String contentType, String url, String postBody) throws IOException, EncoderException {

        HttpPost httpPost = new HttpPost(url);
        if(headerMap!=null){
            Iterator<Map.Entry<String, String>> headers = headerMap.entrySet().iterator();
            while (headers.hasNext()){
                Map.Entry<String, String> header = headers.next();
                httpPost.setHeader(header.getKey(),header.getValue());
            }
        }
        StringEntity se = new StringEntity(postBody,"UTF-8");

        /**
         * Http Header里的Content-Type一般有这三种：
         *
         * application/x-www-form-urlencoded：数据被编码为名称/值对。这是标准的编码格式。
         * multipart/form-data：这种方式提交时，参数会被分割成多块，每一个参数块都有自己独立的content-type，此方式可用于提交普通表单和文件上传，并加上分割符(boundary)。
         * text/plain： 数据以纯文本形式（text/json/xml/html）进行编码（即 application/json 格式），其中不含任何控件或格式字符。postman软件里标的是RAW。
         *
         * form 的 enctype 属性为编码方式，常用有两种：application/x-www-form-urlencoded 和 multipart/form-data，默认为application/x-www-form-urlencoded。
         *
         * 当action为get时候，浏览器用x-www-form-urlencoded的编码方式把form数据转换成一个字串（name1=value1&name2=value2...），
         * 然后把这个字串追加到url后面，用?分割，加载这个新的url。
         *
         * 当action为post时候，浏览器把form数据封装到http body中，然后发送到server。如果没有type=file的控件，用默认的application/x-www-form-urlencoded就可以了。
         * 但是如果有type=file的话，就要用到multipart/form-data了。
         *
         * 当action为post，且Content-Type类型是multipart/form-data，浏览器会把整个表单以控件为单位分割，并为每个部分加上Content-Disposition(form-data或者file)，
         * Content-Type(默认为text/plain)，name(控件name)等信息，并加上分割符(boundary)。
          */
        if(contentType!=null){
            se.setContentType(contentType);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        }else {
            se.setContentType("text/json;charset=UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        }
        httpPost.setEntity(se);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        return body;
    }


    public static void downLoadWebPicture(String url,String path) {

        // 获取输入流
        InputStream inputStream = getInputStream(url);
        byte[] date = new byte[1024];
        int len = 0;
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(path);
            while ((len = inputStream.read(date)) != -1) {
                fileOutputStream.write(date, 0, len);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    public static InputStream getInputStream(String urlPath) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(urlPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置连接网络的超时时间
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                inputStream = httpURLConnection.getInputStream();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inputStream;
    }


    @SuppressWarnings("rawtypes")
    public static String localIp(){
        String ip = null;
        Enumeration allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
                for (InterfaceAddress add : InterfaceAddress) {
                    InetAddress Ip = add.getAddress();
                    if (Ip != null && Ip instanceof Inet4Address) {
                        ip = Ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
        }
        return ip;
    }

    public static  void main(String[] args){

        try {
            // json格式数据，在 controller中接收时，也必须是一个字符串然后再转为 json
            String data = getPostData("http://localhost:8080/site/findSite_allow","{\"name\":\"宝盛商业广场1\",\"num\":\"3205021806061121\"}");
            //String data = getData("http://localhost:8080/site/findSite_allow?name=宝盛商业广场1&num=3205021806061121");
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        }
    }

    public static void post(String dataMonitorUrl, Map<String, Object> param, int i) {
    }
}
