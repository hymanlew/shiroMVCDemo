package hyman.config.freemarker;

import freemarker.core.Environment;
import freemarker.template.*;
import hyman.config.CustomException;
import hyman.utils.DateTypeEditor;
import hyman.utils.StringUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;

import java.util.*;

// <p><b>类描述：</b>Freemarker标签工具类</p>
public abstract class AbstractDirectiveUtils {

    /**
     * 输出参数：对象数据
     */
    public static final String OUT_BEAN = "tag_bean";
    /**
     * 输出参数：列表数据
     */
    public static final String OUT_LIST = "tag_list";
    /**
     * 输出参数：分页数据
     */
    public static final String OUT_PAGINATION = "tag_pagination";

    /**
     * 将params的值复制到variable中
     *
     * @param env Environment
     * @param params 参数map
     * @throws TemplateException 参数名称
     * @return 原Variable中的值
     */
    public static Map<String, TemplateModel> addParamsToVariable(Environment env, Map<String, TemplateModel> params)
            throws TemplateException {
        Map<String, TemplateModel> origMap = new HashMap<String, TemplateModel>();
        if (params.size() <= 0) {
            return origMap;
        }
        Set<Map.Entry<String, TemplateModel>> entrySet = params.entrySet();
        String key;
        TemplateModel value;
        for (Map.Entry<String, TemplateModel> entry : entrySet) {
            key = entry.getKey();
            value = env.getVariable(key);
            if (value != null) {
                origMap.put(key, value);
            }
            env.setVariable(key, entry.getValue());
        }
        return origMap;
    }

    /**
     * 将variable中的params值移除
     *
     * @param env Environment
     * @param params 参数map
     * @param origMap 参数map
     * @throws TemplateException 模板异常
     */
    public static void removeParamsFromVariable(Environment env, Map<String, TemplateModel> params,
                                                Map<String, TemplateModel> origMap) throws TemplateException {
        if (params.size() <= 0) {
            return;
        }
        for (String key : params.keySet()) {
            env.setVariable(key, origMap.get(key));
        }
    }

    /**
     * 获得 RequestContext，ViewResolver 中的 exposeSpringMacroHelpers 必须为true
     *
     * @param env Freemarker Environment
     * @throws TemplateException 模板异常
     * @return RequestContext
     */
    public static RequestContext getContext(Environment env) throws TemplateException {
        TemplateModel ctx = env.getGlobalVariable(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE);
        if (ctx instanceof AdapterTemplateModel) {
            return (RequestContext) ((AdapterTemplateModel) ctx).getAdaptedObject(RequestContext.class);
        } else {
            throw new TemplateModelException(
                    "RequestContext '" + AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE + "' not found in DataModel.");
        }
    }

    /**
     *
     * <p><b>方法描述：</b>获取字符串类型值</p>
     * @param name 参数名称
     * @param params 参数map
     * @return String
     * @throws TemplateException 模板异常
     */
    public static String getString(String name, Map<String, TemplateModel> params) throws TemplateException {
        TemplateModel model = params.get(name);
        if (model == null) {
            return null;
        }
        if (model instanceof TemplateScalarModel) {
            return ((TemplateScalarModel) model).getAsString();
        } else if ((model instanceof TemplateNumberModel)) {
            return ((TemplateNumberModel) model).getAsNumber().toString();
        } else {
            throw new CustomException(name);
        }
    }

    /**
     *
     * <p><b>方法描述：</b>获取长整型值</p>
     * @param name 参数名称
     * @param params 参数map
     * @return Long
     * @throws TemplateException 模板异常
     */
    public static Long getLong(String name, Map<String, TemplateModel> params) throws TemplateException {
        TemplateModel model = params.get(name);
        if (model == null) {
            return null;
        }
        if (model instanceof TemplateScalarModel) {
            String s = ((TemplateScalarModel) model).getAsString();
            if (StringUtils.isBlank(s)) {
                return null;
            }
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw new CustomException(name);
            }
        } else if (model instanceof TemplateNumberModel) {
            return ((TemplateNumberModel) model).getAsNumber().longValue();
        } else {
            throw new CustomException(name);
        }
    }

    /**
     *
     * <p><b>方法描述：</b>获取整型值</p>
     * @param name 参数名称
     * @param params 参数map
     * @return Integer值
     * @throws TemplateException 模板异常
     */
    public static Integer getInt(String name, Map<String, TemplateModel> params) throws TemplateException {
        TemplateModel model = params.get(name);
        if (model == null) {
            return null;
        }
        if (model instanceof TemplateScalarModel) {
            String s = ((TemplateScalarModel) model).getAsString();
            if (StringUtils.isBlank(s)) {
                return null;
            }
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new CustomException(name);
            }
        } else if (model instanceof TemplateNumberModel) {
            return ((TemplateNumberModel) model).getAsNumber().intValue();
        } else {
            throw new CustomException(name);
        }
    }

    /**
     *
     * <p><b>方法描述：</b>获取整型数组</p>
     * @param name 参数名称
     * @param params 参数map
     * @return Integer[]
     * @throws TemplateException 模板异常
     */
    public static Integer[] getIntArray(String name, Map<String, TemplateModel> params) throws TemplateException {
        String str = AbstractDirectiveUtils.getString(name, params);
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String[] arr = StringUtils.split(str, ',');
        Integer[] ids = new Integer[arr.length];
        int i = 0;
        try {
            for (String s : arr) {
                ids[i++] = Integer.valueOf(s);
            }
            return ids;
        } catch (NumberFormatException e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     *
     * <p><b>方法描述：</b>获取布尔类型值</p>
     * @param name 参数名称
     * @param params 参数map
     * @return Boolean类型值
     * @throws TemplateException 模板异常
     */
    public static Boolean getBool(String name, Map<String, TemplateModel> params) throws TemplateException {
        TemplateModel model = params.get(name);
        if (model == null) {
            return null;
        }
        if (model instanceof TemplateBooleanModel) {
            return ((TemplateBooleanModel) model).getAsBoolean();
        } else if (model instanceof TemplateNumberModel) {
            return !(((TemplateNumberModel) model).getAsNumber().intValue() == 0);
        } else if (model instanceof TemplateScalarModel) {
            String s = ((TemplateScalarModel) model).getAsString();
            // 空串应该返回null还是true呢？
            if (!StringUtils.isBlank(s)) {
                return !(s.equals("0") || s.equalsIgnoreCase("false") || s.equalsIgnoreCase("f"));
            } else {
                return null;
            }
        } else {
            throw new CustomException(name);
        }
    }

    /**
     *
     * <p><b>方法描述：</b>获取日期类型值</p>
     * @param name 参数名称
     * @param params 参数map
     * @return Date Date类型数据
     * @throws TemplateException 模板异常
     */
    public static Date getDate(String name, Map<String, TemplateModel> params) throws TemplateException {
        TemplateModel model = params.get(name);
        if (model == null) {
            return null;
        }
        if (model instanceof TemplateDateModel) {
            return ((TemplateDateModel) model).getAsDate();
        } else if (model instanceof TemplateScalarModel) {
            DateTypeEditor editor = new DateTypeEditor();
            editor.setAsText(((TemplateScalarModel) model).getAsString());
            return (Date) editor.getValue();
        } else {
            throw new CustomException(name);
        }
    }

    /**
     *
     * <p><b>方法描述：</b>获取keys</p>
     * @param prefix 前缀
     * @param params 参数
     * @return Set
     */
    public static Set<String> getKeysByPrefix(String prefix, Map<String, TemplateModel> params) {
        Set<String> keys = params.keySet();
        Set<String> startWithPrefixKeys = new HashSet<String>();
        if (keys == null) {
            return null;
        }
        for (String key : keys) {
            if (key.startsWith(prefix)) {
                startWithPrefixKeys.add(key);
            }
        }
        return startWithPrefixKeys;
    }
}
