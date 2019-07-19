package hyman.customFilter;

import hyman.entity.ResponseData;
import org.springframework.core.NamedThreadLocal;

import java.util.List;

public class ThreadLocalDemo {
    /**
     * 线程安全示例
     */

    // css文件路径集合
    private static ThreadLocal<List<String>> CSS_FILE_THREAD = new NamedThreadLocal<List<String>>("ThreadLocal StartTime");

    // js文件路径集合
    private static ThreadLocal<List<String>> JS_FILE_THREAD = new NamedThreadLocal<List<String>>("ThreadLocal StartTime");

    public static void main(String[] args) {

        // java 泛型示例，获取 bean 对象
        try {
            ResponseData data = getControl("ResponseData".toLowerCase(), "type");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    private static <T> T getControl(String name, String type)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        String assemblyName = "hyman.entity.";
        String fullName = assemblyName + name + "." + type;
        Class<?> demo = Class.forName(fullName);
        Object ect = demo.newInstance();
        return (T) ect;
    }

}


/**
 * 定义校验顺序，如果AddGroup组失败，则UpdateGroup组不会再校验
 AddGroup.class, UpdateGroup.class 都是自定义的接口。
 */
//@GroupSequence({AddGroup.class, UpdateGroup.class})
interface Group {
}