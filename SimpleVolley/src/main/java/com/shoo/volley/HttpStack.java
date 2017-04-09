package com.shoo.volley;

import org.apache.http.HttpResponse;

import java.util.Map;

/**
 * Created by Shoo on 17-4-9.
 */
public interface HttpStack {

    HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws VolleyError;

}
