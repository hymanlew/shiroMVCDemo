package hyman.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>类描述：</b>Rest请求工具类</p>
 */
public class RestRequestUtil {

    /** rest 地址 */
    public static final String URL_1 = "http://" + "10.0.4.231";
    /** rest 端口 */
    public static final String URL_2 = ":9888/services/";

    /** 富文本静态变量  */
    public class FileParams {

        /** 获取富文本文件内容 url */
        public static final String FILE_PREVIEW_TXT = "file/preview-file-txt";

        /** 保存富文本文件内容 url */
        public static final String UPLOAD_FILE_TXT = "file/upload-file-txt";
    }

    /**
     *
     * <p><b>方法描述：</b>获取文本内容</p>
     * @param id  文本id
     * 说明：
     * 调用保存文本方法的时候，返回来的id   @see uploadTxt()方法
     * @return 返回 文本内容
     */
    public static String previewTxtById(String id) throws Exception{
        String url = URL_1 + URL_2 + FileParams.FILE_PREVIEW_TXT;
        // String txt = "";

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        String resultJsonStr = HttpUtil.getPostData(url, params);

        // JSONObject jsonObj = JSONObject.parseObject(resultJsonStr);
        // if (resultJsonStr != null && !"null".equals(resultJsonStr)){
        // if (jsonObj != null
        // && jsonObj.get("state") != null
        // && "1".equals(jsonObj.get("state").toString())){
        // txt = jsonObj.getString("data");
        // }
        // }
        return resultJsonStr;
    }

    /**
     * <p><b>方法描述：</b>保存txt</p>
     * @param params 提交参数
     * params结构说明：
     * uid   当前登录用户
     * code  当前项目编码（自定义）
     * txt   需要存储的内容
     * 代码范例：
     * Map<String, Object> params = new HashMap<String, Object>();
     * params.put("uid", acc.getId());
     * params.put("code", "310");
     * params.put("txt", feedbackVo.getDescription());
     *
     * @return 文件id
     */
    public static String uploadTxt(Map<String, String> params) throws Exception{
        String url = URL_1 + URL_2 + FileParams.UPLOAD_FILE_TXT;
        String txtId = "";
        String resultJsonStr = HttpUtil.getPostData(url, params);
        JSONObject jsonObj = JSONObject.parseObject(resultJsonStr);
        if (resultJsonStr != null && !"null".equals(resultJsonStr)) {
            if (jsonObj != null && jsonObj.get("state") != null && "1".equals(jsonObj.get("state").toString())) {
                txtId = jsonObj.getString("data");
            }
        }
        return txtId;
    }
}
