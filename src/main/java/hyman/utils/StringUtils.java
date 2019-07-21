package hyman.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hyman.config.CustomException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final char SEPARATOR = '_';

    /**
     *
     * <p><b>方法描述：</b>获取json字符串中的id</p>
     * @param jsonStr json字符串
     * @return 有id则返回id值，没有返回空
     */
    public static String getJsonId(String jsonStr) {
        String returnStr = getJsonId(jsonStr, 0);
        return returnStr;
    }

    /**
     *
     * <p><b>方法描述：</b>获取json字符串中指定索引值的id</p>
     * @param jsonStr json字符串
     * @param index 索引值
     * @return 有id则返回id值，没有返回空
     */
    public static String getJsonId(String jsonStr, int index) {
        if (isBlank(jsonStr)) {
            return "";
        }
        try {
            JSONArray array = JSON.parseArray(jsonStr);
            JSONObject obj = JSON.parseObject(array.get(index).toString());
            return obj.getString("id");
        } catch (Exception e) {
            throw new CustomException("json字符串格式异常");
        }
    }

    /**
     *
     * <p><b>方法描述：</b>获取json字符串中的name</p>
     * @param jsonStr json字符串
     * @return 有name则返回name值，没有返回空
     */
    public static String getJsonName(String jsonStr) {
        String returnStr = getJsonName(jsonStr, 0);
        return returnStr;
    }

    /**
     *
     * <p><b>方法描述：</b>获取json字符串中指定索引值的name</p>
     * @param jsonStr json字符串
     * @param index 索引值
     * @return 有name则返回name值，没有返回空
     */
    public static String getJsonName(String jsonStr, int index) {
        if (isBlank(jsonStr)) {
            return "";
        }
        try {
            JSONArray array = JSON.parseArray(jsonStr);
            JSONObject obj = JSON.parseObject(array.get(index).toString());
            return obj.getString("name");
        } catch (Exception e) {
            throw new CustomException("json字符串格式异常");
        }
    }

    /**
     *
     * <p><b>方法描述：</b>方法的功能描述</p>
     * @return
     */
    public static String getString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
     * 2. 将字段中Date类型转换为String类型
     * <p><b>方法描述：</b>初始化数据绑定</p>
     * @param binder binder
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
            }

            @Override
            public String getAsText() {
                Object value = getValue();
                return value != null ? value.toString() : "";
            }
        });
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    setValue(format.parse(text));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *
     * <p><b>方法描述：</b>字符串中是否包含中文</p>
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     *
     * <p><b>方法描述：</b>判断是否是合法域名</p>
     * @param str
     * @return
     */
    public static boolean isLegalDomain(String str) {
        boolean isWord = str.matches("[a-zA-Z0-9_-]+");
        return isWord;
    }

    /**
     * 驼峰命名法工具
     * @return
     * 		toCamelCase("hello_world") == "helloWorld"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        s = sb.toString();
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     * @return
     * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     * @return
     * 		toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }
            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
}
