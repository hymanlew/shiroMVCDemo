//package hyman.utils;
//
//import javax.servlet.http.HttpServletRequest;
//
//public class UserAgentUtils {
//
//    /**
//     * <p><b>方法描述：</b>获取用户代理对象</p>
//     * @param request HttpServletRequest对象
//     * @return UserAgent对象
//     */
//    public static UserAgent getUserAgent(HttpServletRequest request){
//        return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
//    }
//
//    /**
//     *
//     * <p><b>方法描述：</b>获取设备类型</p>
//     * @param request HttpServletRequest对象
//     * @return DeviceType的对象
//     */
//    public static DeviceType getDeviceType(HttpServletRequest request){
//        return getUserAgent(request).getOperatingSystem().getDeviceType();
//    }
//
//    /**
//     *
//     * <p><b>方法描述：</b>是否是PC</p>
//     * @param request HttpServletRequest对象
//     * @return true or false
//     */
//    public static boolean isComputer(HttpServletRequest request){
//        return DeviceType.COMPUTER.equals(getDeviceType(request));
//    }
//
//    /**
//     *
//     * <p><b>方法描述：</b>是否是手机</p>
//     * @param request HttpServletRequest对象
//     * @return true or false
//     */
//    public static boolean isMobile(HttpServletRequest request){
//        return DeviceType.MOBILE.equals(getDeviceType(request));
//    }
//
//    /**
//     *
//     * <p><b>方法描述：</b>是否是平板</p>
//     * @param request HttpServletRequest对象
//     * @return true or false
//     */
//    public static boolean isTablet(HttpServletRequest request){
//        return DeviceType.TABLET.equals(getDeviceType(request));
//    }
//
//    /**
//     *
//     * <p><b>方法描述：</b>是否是手机和平板</p>
//     * @param request HttpServletRequest对象
//     * @return true or false
//     */
//    public static boolean isMobileOrTablet(HttpServletRequest request){
//        DeviceType deviceType = getDeviceType(request);
//        return DeviceType.MOBILE.equals(deviceType) || DeviceType.TABLET.equals(deviceType);
//    }
//
//    /**
//     *
//     * <p><b>方法描述：</b>获取浏览类型</p>
//     * @param request HttpServletRequest对象
//     * @return Browser对象
//     */
//    public static Browser getBrowser(HttpServletRequest request){
//        return getUserAgent(request).getBrowser();
//    }
//
//    /**
//     *
//     * <p><b>方法描述：</b>是否IE版本是否小于等于IE8</p>
//     * @param request HttpServletRequest对象
//     * @return true or false
//     */
//    public static boolean isLteIE8(HttpServletRequest request){
//        Browser browser = getBrowser(request);
//        return Browser.IE5.equals(browser) || Browser.IE6.equals(browser)
//                || Browser.IE7.equals(browser) || Browser.IE8.equals(browser);
//    }
//}
