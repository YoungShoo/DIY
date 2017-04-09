package com.shoo.volley;

import java.util.Map;

/**
 * Created by Shoo on 17-4-9.
 */
public class NetworkResponse {

    public int statusCode;
    public byte[] data;
    public Map<String, String> headers;
    public boolean notModified;
    public long networkTimeMs;

    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean
            notModified, long networkTimeMs) {
        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
        this.notModified = notModified;
        this.networkTimeMs = networkTimeMs;
    }
}
