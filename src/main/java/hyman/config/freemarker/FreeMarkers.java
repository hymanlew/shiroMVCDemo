package hyman.config.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import hyman.controller.BaseController;
import hyman.utils.Constant;
import hyman.utils.Exceptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

// 渲染 freemarker 模版
public class FreeMarkers {

    public static String render(String templateString, Map<String, ?> model, Configuration configuration) {
        try {
            Template template = new Template("name", new StringReader(templateString), configuration);
            return renderTemplate(template, model);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }
    public static String renderString(String templateString, Map<String, ?> model) {
        return render(templateString, model, new Configuration(Configuration.VERSION_2_3_23));
    }
    public static String renderString(String templateName, Map<String, ?> model, Configuration configuration) {
        try {
            Template template = configuration.getTemplate(templateName);
            return renderTemplate(template, model);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }
    public static String renderTemplate(Template template, Object model) {
        try {
            StringWriter result = new StringWriter();
            template.process(model, result);
            return result.toString();
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }
}
