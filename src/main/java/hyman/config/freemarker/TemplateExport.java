package hyman.config.freemarker;

import freemarker.template.*;
import hyman.config.CustomException;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * <p><b>类描述：</b>根据模板导出文件</p>
 */
public class TemplateExport {

    /**
     * 模板参数对象
     */
    private static Configuration configuration = null;

    /**
     *
     * <p><b>方法描述：</b>以流的方式输出文件</p>
     * @param request request对象
     * @param response response对象
     * @param dataMap 需装载的数据
     * @param expType 导出文件类型 "excel"、"word"
     * @param templateFolderName 模板文件所在目录名称(注:模板文件放在WEB-INF下)
     * @param templateFileName 模板文件名称
     * @param exportFileName 输出文件名称
     */
    public static void exportStream(HttpServletRequest request, HttpServletResponse response,
                                    Map<String, Object> dataMap, String expType, String templateFolderName, String templateFileName,
                                    String exportFileName) {
        configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setServletContextForTemplateLoading(request.getSession().getServletContext(), "WEB-INF/" + templateFolderName);
        Template t = null;
        OutputStream os = null;

        try {
            t = configuration.getTemplate(templateFileName, "UTF-8");
            os = response.getOutputStream();
            response.reset();
            response.setContentType("application/vnd.ms-" + expType + ";charset=UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment; filename=" + new String((exportFileName).getBytes("gb2312"), "iso8859-1"));
            Writer out = null;
            out = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            t.process(dataMap, out);
        } catch (TemplateNotFoundException e) {
            throw new CustomException(e.getMessage());
        } catch (MalformedTemplateNameException e) {
            throw new CustomException(e.getMessage());
        } catch (IOException e) {
            throw new CustomException(e.getMessage());
        } catch (TemplateException e) {
            throw new CustomException(e.getMessage());
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                throw new CustomException(e.getMessage());
            }
            try {
                os.flush();
            } catch (IOException e) {
                throw new CustomException(e.getMessage());
            }
        }
    }

    /**
     *
     * <p><b>方法描述：</b>返回图片编码后的字符串</p>
     * @param imgFilePath 图片路径
     * @return 图片编码后的字符串
     */
    public static String getImageStr(String imgFilePath) {
        String imgFile = imgFilePath;
        InputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
        } catch (FileNotFoundException e) {
            throw new CustomException(e.getMessage());
        } catch (IOException e) {
            throw new CustomException(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new CustomException(e.getMessage());
            }
        }
        return Base64.encodeBase64String(data);
    }
}
