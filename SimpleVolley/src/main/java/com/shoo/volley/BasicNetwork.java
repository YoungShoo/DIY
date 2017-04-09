package com.shoo.volley;

import android.os.SystemClock;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.cookie.DateUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Shoo on 17-4-9.
 */

public class BasicNetwork implements Network {

    private static final String HEADER_IF_MODIFIED_SINCE = "If-Modified_Since";

    private final HttpStack mHttpStack;

    public BasicNetwork(HttpStack httpStack) {
        mHttpStack = httpStack;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long startTimeMs = SystemClock.elapsedRealtime();

        try {
            Map<String, String> cacheHeaders = new HashMap<>();
            addCacheHeaders(cacheHeaders, request.getCacheEntry());
            HttpResponse httpResponse = mHttpStack.performRequest(request, cacheHeaders);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            Map<String, String> headers = convertHeaders(httpResponse.getAllHeaders());

            if (HttpStatus.SC_NOT_MODIFIED == statusCode) {
                Cache.Entry entry = request.getCacheEntry();
                if (entry == null) {
                    return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, null, headers, true,
                            SystemClock.elapsedRealtime() - startTimeMs);
                }
                entry.responseHeaders.putAll(headers);
                return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, entry.data, entry
                        .responseHeaders, true, SystemClock.elapsedRealtime() - startTimeMs);
            }

            byte[] data = entityToBytes(httpResponse.getEntity());

            // why? what about 404?
            if (statusCode < HttpStatus.SC_OK || statusCode > HttpStatus.SC_MULTIPLE_CHOICES - 1) {
                throw new IOException();
            }

            return new NetworkResponse(HttpStatus.SC_MULTIPLE_CHOICES, data, headers, false, SystemClock
                    .elapsedRealtime() - startTimeMs);
        } catch (IOException e) {
            throw new VolleyError(e);
        }
    }

    private void addCacheHeaders(Map<String, String> cacheHeaders, Cache.Entry cacheEntry) {
        if (cacheEntry == null) {
            return;
        }

        if (cacheEntry.lastModified > 0) {
            Date date = new Date(cacheEntry.lastModified);
            cacheHeaders.put(HEADER_IF_MODIFIED_SINCE, DateUtils.formatDate(date));
        }
    }

    private Map<String, String> convertHeaders(Header[] allHeaders) {
        Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Header header : allHeaders) {
            headers.put(header.getName(), header.getValue());
        }

        return headers;
    }

    private byte[] entityToBytes(HttpEntity entity) {
        // TODO: 17-4-9
        return new byte[0];
    }
}
