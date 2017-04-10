package com.shoo.volley;

import android.text.TextUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shoo on 17-4-9.
 */

public class HurlStack implements HttpStack {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws VolleyError {

        String url = request.getUrl();
        try {
            URL parsedUrl = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) parsedUrl.openConnection();
            setRequestMethod(connection, request);
            setConnectionHeaders(connection, request.getHeaders(), additionalHeaders);

            // status line
            ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
            int responseCode = connection.getResponseCode();
            String msg = connection.getResponseMessage();
            StatusLine statusLine = new BasicStatusLine(version, responseCode, msg);
            HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            // headers
            addResponseHeaders(connection, httpResponse);

            // content
            if (hasResponseBody(request.getMethod(), responseCode)) {
                httpResponse.setEntity(entityFromConnection(connection));
            }

            return httpResponse;
        } catch (IOException e) {
            throw new VolleyError(e);
        }
    }

    private boolean hasResponseBody(int method, int responseCode) {
        return Request.Method.HEAD != method
                && !(HttpStatus.SC_CONTINUE >= responseCode && HttpStatus.SC_OK < responseCode)
                && responseCode != HttpStatus.SC_NO_CONTENT
                && responseCode != HttpStatus.SC_NOT_MODIFIED;
    }

    private void addResponseHeaders(HttpURLConnection connection, HttpResponse
            httpResponse) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            if (!TextUtils.isEmpty(entry.getKey())) {
                httpResponse.addHeader(new BasicHeader(entry.getKey(), entry.getValue().get(0)));
            }
        }
    }

    private HttpEntity entityFromConnection(HttpURLConnection connection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        try {
            entity.setContent(connection.getInputStream());
        } catch (IOException e) {
            entity.setContent(connection.getErrorStream());
        }
        entity.setContentLength(connection.getContentLength());
        entity.setContentType(connection.getContentType());
        entity.setContentEncoding(connection.getContentEncoding());
        return entity;
    }

    private void setConnectionHeaders(HttpURLConnection connection, Map<String, String>
            requestHeaders, Map<String, String> additionalHeaders) {
        Map<String, String> headers = new HashMap<>();
        headers.putAll(requestHeaders);
        headers.putAll(additionalHeaders);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private void setRequestMethod(HttpURLConnection connection, Request<?> request) throws
            IOException {
        switch (request.getMethod()) {
            case Request.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfNeeded(connection, request);
                break;
            case Request.Method.GET:
            default:
                connection.setRequestMethod("GET");
                break;
        }
    }

    private void addBodyIfNeeded(HttpURLConnection connection, Request<?> request) throws
            IOException {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONTENT_TYPE, request.getBodyContentType());
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body);
        }
    }
}
