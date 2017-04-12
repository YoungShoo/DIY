package com.shoo.volley;

import android.text.TextUtils;

import org.apache.http.protocol.HTTP;

import java.util.Map;

/**
 * Created by Shoo on 17-4-10.
 */
public class HttpHeaderParser {

    public static Cache.Entry parseCacheHeaders(NetworkResponse networkResponse) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = networkResponse.headers;

        long staleWhileRevalidate = 0;
        long maxAge = 0;
        boolean mustRevalidate = false;
        boolean hasCacheControl = false;

        String eTag = headers.get(HttpHeaders.ETAG);

        String headerValue = headers.get(HttpHeaders.CACHE_CONTROL);
        if (!TextUtils.isEmpty(headerValue)) {
            hasCacheControl = true;
            String[] tokens = headerValue.trim().split(",");
            for (String token : tokens) {
                if ("no-cache".equals(token) || "no-store".equals(token)) {
                    return null;
                }
                if (token.startsWith("max-age=")) {
                    try {
                        maxAge = Long.parseLong(token.substring("max-age=".length()));
                    } catch (Exception e) {
                    }
                } else if (token.startsWith("stale-while-revalidate=")) {
                    try {
                        staleWhileRevalidate = Long.parseLong(token.substring("stale-while-revalidate".length()));
                    } catch (Exception e) {
                    }
                } else if ("must-revalidate".equals(token) || "proxy-revalidate".equals(token)) {
                    mustRevalidate = true;
                }
            }
        }

        long serverDate = parseDateAsEpoch(headers.get(HttpHeaders.DATE));
        long serverExpires = parseDateAsEpoch(headers.get(HttpHeaders.EXPIRES));
        long lastModified = Long.parseLong(headers.get(HttpHeaders.LAST_MODIFIED));

        long softExpires = 0;
        long finalExpires = 0;
        if (hasCacheControl) {
            softExpires = now + maxAge * 1000;
            finalExpires = mustRevalidate ? softExpires : softExpires + staleWhileRevalidate * 1000;
        } else if (serverDate > 0 && serverExpires - serverDate >= 0) {
            softExpires = now + (serverExpires - serverDate);
            finalExpires = softExpires;
        }

        Cache.Entry entry = new Cache.Entry();
        entry.data = networkResponse.data;
        entry.eTag = eTag;
        entry.softExpires = softExpires;
        entry.finalExpires = finalExpires;
        entry.serverDate = serverDate;
        entry.lastModified = lastModified;
        entry.responseHeaders = headers;
        return entry;
    }

    private static long parseDateAsEpoch(String headerValue) {
        try {
            return Long.parseLong(headerValue);
        } catch (Exception e) {
            return 0;
        }
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
                if (pair.length == 2 && "charset".equals(pair[0])) {
                    return pair[1];
                }
            }
        }

        return HTTP.DEFAULT_CONTENT_CHARSET;
    }

    public static String parseLocation(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }

        return headers.get(HttpHeaders.LOCATION);
    }
}
