package hyman.config.freemarker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import hyman.utils.Constant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * TemplateDirectiveModel 接口是 freemarker 自定标签或者自定义指令的核心处理接口。通过实现该接口，用户可以自定义标签（指令）
 * 进行任意操作，任意文本写入模板的输出。
 *
 * FreeMarker 不仅可以在前端的模板页中定义宏（批量的重复性的操作自动实现），还可以通过扩展其接口在后端实现宏。这样就好比让你的
 * 模板页具备了从前端再次回到后端的能力。我们无需在各个 controller 的各个接口中去重复的向 model 中添加所需的参数数据，而是当
 * FreeMarker 渲染模板页时遇到相应的宏它可以回到后端去调用相应的方法取到所需的数据。
 * 即渲染页面时，统一优先配置好页面中的参数，类似于初始化 bean 时，添加全局变量。
 */
@Component("filePreview")
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
