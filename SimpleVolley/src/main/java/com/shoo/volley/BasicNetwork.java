package com.shoo.volley;

import android.os.SystemClock;

import com.shoo.volley.error.AuthFailError;
import com.shoo.volley.error.ConnectionError;
import com.shoo.volley.error.NetworkError;
import com.shoo.volley.error.RedirectError;
import com.shoo.volley.error.ServerError;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.cookie.DateUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

        while (true) {
            HttpResponse httpResponse = null;
            Map<String, String> responseHeaders = null;
            byte[] responseContent = null;
            try {
                Map<String, String> cacheHeaders = new HashMap<>();
                addCacheHeaders(cacheHeaders, request.getCacheEntry());
                httpResponse = mHttpStack.performRequest(request, cacheHeaders);

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                responseHeaders = convertHeaders(httpResponse.getAllHeaders());

                if (HttpStatus.SC_NOT_MODIFIED == statusCode) {
                    Cache.Entry entry = request.getCacheEntry();
                    if (entry == null) {
                        return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, null, responseHeaders, true,
                                SystemClock.elapsedRealtime() - startTimeMs);
                    }
                    entry.responseHeaders.putAll(responseHeaders);
                    return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, entry.data, entry
                            .responseHeaders, true, SystemClock.elapsedRealtime() - startTimeMs);
                }

                responseContent = entityToBytes(httpResponse.getEntity());

                if (statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                    throw new IOException();
                }

                return new NetworkResponse(HttpStatus.SC_OK, responseContent, responseHeaders, false, SystemClock
                        .elapsedRealtime() - startTimeMs);
            } catch (IOException e) {
                if (httpResponse == null) {
                    throw new ConnectionError(e);
                }

                if (responseContent == null) {
                    throw new NetworkError(e);
                }

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                NetworkResponse networkResponse = new NetworkResponse(statusCode, responseContent, responseHeaders,
                        false, SystemClock.elapsedRealtime() - startTimeMs);

                if (statusCode >= HttpStatus.SC_MULTIPLE_CHOICES && statusCode < HttpStatus.SC_BAD_REQUEST) {
                    // 3xx
                    attemptRetryOnException(request, new RedirectError(networkResponse));
                    continue;
                } else if (statusCode >= HttpStatus.SC_BAD_REQUEST && statusCode < HttpStatus
                        .SC_INTERNAL_SERVER_ERROR) {
                    // 4xx
                    if (HttpStatus.SC_UNAUTHORIZED == statusCode || HttpStatus.SC_FORBIDDEN == statusCode) {
                        attemptRetryOnException(request, new AuthFailError(networkResponse));
                        continue;
                    }
                } else if (statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    // 5xx
                    throw new ServerError(networkResponse);
                }

                throw new VolleyError(e);
            }
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

    private byte[] entityToBytes(HttpEntity entity) throws IOException {
        if (entity == null) {
            return new byte[0];
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream inputStream = entity.getContent();
        byte[] buffer = new byte[1024];
        int count;
        while ((count = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
        }
        entity.consumeContent();
        return baos.toByteArray();
    }

    private void attemptRetryOnException(Request<?> request, VolleyError error) throws VolleyError {
        if (!request.retry(error)) {
            throw error;
        }
    }
}
