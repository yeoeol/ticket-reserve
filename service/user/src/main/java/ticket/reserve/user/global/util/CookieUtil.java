package ticket.reserve.user.global.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie setHttpOnlyCookie(String key, String value, int expiry) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiry);
        return cookie;
    }

}
