package io.github.jiarus.util;

import org.apache.commons.lang.StringUtils;

import java.net.URI;

/**
 * @author zhangjiaru
 * @date: 2021/01/30
 */
public class HttpUtil {
    
    private static String getHost(String url) {
        if (!(StringUtils.startsWithIgnoreCase(url, "http://") || StringUtils
                .startsWithIgnoreCase(url, "https://"))) {
            url = "http://" + url;
        }
        String returnVal = StringUtils.EMPTY;
        try {
            URI uri = new URI(url);
            returnVal = uri.getHost();
        } catch (Exception e) {
        }
        if ((StringUtils.endsWithIgnoreCase(returnVal, ".html") || StringUtils
                .endsWithIgnoreCase(returnVal, ".htm"))) {
            returnVal = StringUtils.EMPTY;
        }
        return returnVal;
    }
    
    
}
