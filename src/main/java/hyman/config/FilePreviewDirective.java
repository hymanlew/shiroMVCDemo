package hyman.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import hyman.utils.Constant;
import hyman.utils.DirectiveUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component("cme_file_preview")
public class FilePreviewDirective implements TemplateDirectiveModel {

    private static final Logger logger = LoggerFactory.getLogger(FilePreviewDirective.class);

    private static final String VAL_VALUE = "value";

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String value = DirectiveUtils.getString(VAL_VALUE, params);
        if (value.indexOf("[") < 0) {
            value = "[" + value + "]";
        }
        if (StringUtils.isNotBlank(value)) {
            JSONObject obj = null;
            JSONArray array = null;
            String downHtml = "";
            try {
                array = JSONArray.parseArray(value);
                if (null != array && !array.isEmpty()) {
                    for (Object o : array) {
                        obj = (JSONObject) o;
                        if (obj != null) {
                            switch(obj.getString("fileType").toLowerCase()){
//							case "doc":
//								downHtml += "<a preview='doc' target='_blank' href=\""+ Constant.getProperty("root.path", "") +"file/preview?filetype=doc&path=" + URLEncoder.encode(Constant.DOWNLOAD_PATH,"utf-8") +"&id="+ obj.getString("id") + "\" >"
//										+ obj.getString("originalName") + "</a> &nbsp;";
//								break;
//							case "xls":
//								downHtml += "<a preview='doc' target='_blank' href=\""+ Constant.getProperty("root.path", "") +"file/preview?filetype=xls&path=" + URLEncoder.encode(Constant.DOWNLOAD_PATH,"utf-8") +"&id="+ obj.getString("id") + "\" >"
//										+ obj.getString("originalName") + "</a> &nbsp;";
//								break;
//							case "pdf":
//								downHtml += "<a preview='doc' target='_blank' href=\""+ Constant.getProperty("root.path", "") +"file/preview?filetype=pdf&path=" + URLEncoder.encode(Constant.DOWNLOAD_PATH,"utf-8") +"&id="+ obj.getString("id") + "\" >"
//										+ obj.getString("originalName") + "</a> &nbsp;";
//								break;
                                default:
                                    downHtml += "<a preview-file='"+ obj.getString("fileType").toLowerCase() +"' href=\"" + Constant.DOWNLOAD_PATH + obj.getString("id") + "\" >"
                                            + obj.getString("originalName") + "</a><textarea style='display:none;' class='preview'>"+ value +"</textarea> &nbsp;";
                                    break;
                            }
                        }
                    }
                    env.getOut().write(downHtml);
                }
            } catch (JSONException e) {
                if (logger.isDebugEnabled()) {
                    logger.error(e.getMessage());
                }
            }
        }
    }
}
