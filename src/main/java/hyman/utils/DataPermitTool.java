package hyman.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;
import java.util.Map;

public class DataPermitTool {

    /**
     *
     * @Description:(获取平台id)
     * @param request
     * @return
     * @author:yankai@cmnec.com
     * @date:2018年11月19日
     */
    public static String getDataPermit(HttpServletRequest request, String businessFlag){
        String dataPermit = "";
        try {
            String filePath = request.getSession().getServletContext().getRealPath("resources/json/datapermit.json");
            String content=readJsonData(filePath);
            if(StringUtils.isNotBlank(content)){
                List<Map<String, String>> map = JSON.parseObject(content, new TypeReference<List<Map<String, String>>>() {});
                for (Map<String, String> map2 : map) {
                    if(map2.containsKey(businessFlag)){
                        dataPermit = map2.get(businessFlag);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataPermit;
    }

    public static String readJsonData(String pactFile) throws IOException {
        StringBuffer strbuffer = new StringBuffer();
        File myFile = new File(pactFile);//"D:"+File.separatorChar+"DStores.json"
        if (!myFile.exists()) {
            System.err.println("Can't Find " + pactFile);
        }
        FileInputStream fis=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader in=null;
        try {
            fis = new FileInputStream(pactFile);
            inputStreamReader = new InputStreamReader(fis, "UTF-8");
            in  = new BufferedReader(inputStreamReader);
            String str;
            while ((str = in.readLine()) != null) {
                strbuffer.append(str);  //new String(str,"UTF-8")
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        in.close();
        inputStreamReader.close();
        fis.close();
        return strbuffer.toString();
    }
}
