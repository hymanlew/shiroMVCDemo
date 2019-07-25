package hyman.config.freemarker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import freemarker.core.Environment;
import freemarker.template.*;
import hyman.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 获取图片
@Component("cust_freemark_image")
public class SingleImageDirective implements TemplateDirectiveModel {

    /**
     * 日志操作类
     */
    private static final Logger LOG = LoggerFactory.getLogger(SingleImageDirective.class);

    /**
     * 图片json字符串
     */
    private static final String PARAM_VALUE = "value";

    /**
     * 默认图片地址
     */
    private static final String DEFAULT_VALUE = "default";

    /**
     * 图片访问路径
     */
    @Value("${FilePath}")
    private String filePath;

    /**
     * 静态资源路径
     */
    @Value("${StaticPath}")
    private String staticPath;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String jsonStr = DirectiveUtils.getString(PARAM_VALUE, params);
        String defalut = DirectiveUtils.getString(DEFAULT_VALUE, params);
        boolean flag = false;
        if (StringUtils.isNotBlank(jsonStr)) {
            List<JSONObject> objs = null;
            try {
                objs = JSONArray.parseArray(jsonStr, JSONObject.class);
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.error(e.getMessage());
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    objs = new ArrayList<>();
                    objs.add(jsonObject);
                } catch (Exception e2) {
                    if (LOG.isDebugEnabled()) {
                        LOG.error(e.getMessage());
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", jsonStr);
                    objs = new ArrayList<>();
                    objs.add(jsonObject);
                }
            }
            if (objs != null && !objs.isEmpty()) {
                env.getOut().write(filePath + "showimg/" + objs.get(0).getString("id"));
                flag = true;
            }
        }

        if (!flag && StringUtils.isNotBlank(defalut)) {
            TemplateModel staticModel = env.getDataModel().get("StaticPath");
            if (staticModel != null && staticModel instanceof TemplateScalarModel) {
                env.getOut().write(((TemplateScalarModel) staticModel).getAsString() + defalut);
            } else{
                env.getOut().write(staticPath + defalut);
            }
        }
    }
}
