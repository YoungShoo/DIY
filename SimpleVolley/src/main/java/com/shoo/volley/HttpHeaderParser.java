package com.shoo.volley;

import android.text.TextUtils;

import org.apache.http.protocol.HTTP;

import java.util.Map;

/**
 * Created by Shoo on 17-4-10.
 */
public class HttpHeaderParser {

    private static final String CHARSET = "charset";

    public static Cache.Entry parseResponseHeaders(Map<String, String> headers) {
        // TODO: 17-4-10 Shoo
        return new Cache.Entry();
    }

    public static String parseCharset(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return HTTP.DEFAULT_CONTENT_CHARSET;
        }

        String contentType = headers.get(HTTP.CONTENT_TYPE);
        if (!TextUtils.isEmpty(contentType)) {
            String[] params = contentType.split(";");
            for (String param : params) {
                String[] pair = param.trim().split("=");
                if (pair.length == 2 && CHARSET.equals(pair[0])) {
                    return pair[1];
                }
            }
        }

        return HTTP.DEFAULT_CONTENT_CHARSET;
    }
}
