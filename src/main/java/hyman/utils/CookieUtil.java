package hyman.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    private static final int COOKIE_MAX_AGE = 7 * 24 * 3600;

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        if (null == name) {
            return;
        }
        Cookie cookie = getCookie(request, name);
        if (null != cookie) {
            cookie.setPath("/");
            cookie.setValue("");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies || null == name || name.length() == 0) {
            return null;
        }
        Cookie cookie = null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                cookie = c;
                break;
            }
        }
        return cookie;
    }
    public static void setCookie(HttpServletResponse response, String name, String value) {
        setCookie(response, name, value, COOKIE_MAX_AGE);
    }
    public static void setCookie(HttpServletResponse response, String name, String value, int maxValue) {
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
            Cookie cookie = new Cookie(name, value);
            cookie.setPath("/");
            if (maxValue != 0) {
                cookie.setMaxAge(maxValue);
            } else {
                cookie.setMaxAge(COOKIE_MAX_AGE);
            }
            response.addCookie(cookie);
        }
    }
}
