package hyman.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class BeanUtil extends BeanUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);

    public static Map<String, Object> transBean2Map(Object o) {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = null;
        fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String proName = field.getName();
            Object proValue;
            try {
                proValue = field.get(o);
                map.put(proName.toUpperCase(), proValue);
            } catch (IllegalArgumentException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error(e.getMessage());
                }
            } catch (IllegalAccessException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return map;
    }

    public static Object transMap2Bean(Map<String, Object> map, Object o) {
        if (!map.isEmpty()) {
            for (String k : map.keySet()) {
                Object v = "";
                if (!k.isEmpty()) {
                    v = map.get(k);
                }
                Field[] fields = null;
                fields = o.getClass().getDeclaredFields();
                for (Field field : fields) {
                    int mod = field.getModifiers();
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                    if (field.getName().toUpperCase().equals(k)) {
                        field.setAccessible(true);
                        try {
                            field.set(o, v);
                        } catch (IllegalArgumentException e) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.error(e.getMessage());
                            }
                        } catch (IllegalAccessException e) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.error(e.getMessage());
                            }
                        }
                    }
                }
            }
        }
        return o;
    }
}
