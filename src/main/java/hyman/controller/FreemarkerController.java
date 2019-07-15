package hyman.controller;

import hyman.utils.UserUtils;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/freemarker")
public class FreemarkerController {

    @Resource(name = "freemarkerConfig")
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @RequestMapping("")
    public String dopage(HttpServletRequest request) throws Exception{

        Map<String, Object> map = new HashMap<>();
        String  html = "";
        //html = freemarkers.renderString("/productinfo/list2.htm", map, freeMarkerConfigurer.createConfiguration());
        html = getHtml(request, html);
        return html;
    }

    private String getHtml(HttpServletRequest request, String html) {

        String contentId = request.getParameter("contentId");
        String contentType = request.getParameter("contentType");
        String projectId = request.getParameter("projectId");

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("projectId", projectId);
        paramMap.put("contentId", contentId);
        paramMap.put("userId", UserUtils.getCurrentUserId());
        paramMap.put("contentType", contentType);

        String pageHtml = "";
        //pageHtml = FreeMarkers.renderString("/struct/struct.htm", getSysStaticParamsMap(),
        //        freeMarkerConfigurer.getConfiguration());
        pageHtml = pageHtml.replace("${html!}", html);
        return pageHtml;
    }

    private Map<String, Object> getSysStaticParamsMap(){

        // 存储系统中的常用变量，例如 basepath，filepath，staticpath 等等。
        Map<String, Object> map = new HashMap<>();
        return map;
    }
}
