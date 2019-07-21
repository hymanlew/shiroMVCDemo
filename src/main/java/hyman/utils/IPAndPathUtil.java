package hyman.utils;

import javax.servlet.http.HttpServletRequest;

public class IPAndPathUtil {

    /**
     *
     * <p><b>方法描述：</b>得到请求的IP地址</p>
     * @param request  客户请求
     * @return    请求的IP地址
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(ip)) {
            ip = request.getHeader("Host");
        }
        if (StringUtils.isBlank(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip)) {
            ip = "0.0.0.0";
        }
        return ip;
    }

    /**
     *
     * <p><b>方法描述：</b>得到请求的根目录</p>
     * @param request  客户请求
     * @return    请求的IP地址
     */
    public static String getBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + path;
        return basePath;
    }

    /**
     *
     * <p><b>方法描述：</b>得到结构目录</p>
     * @param request  客户请求
     * @return    请求的IP地址
     */
    public static String getContextPath(HttpServletRequest request) {
        String path = request.getContextPath();
        return path;
    }
}
