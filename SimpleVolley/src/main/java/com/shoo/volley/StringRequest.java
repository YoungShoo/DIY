package com.shoo.volley;

import java.io.UnsupportedEncodingException;

/**
 * Created by Shoo on 17-4-10.
 */

public class StringRequest extends Request<String> {

    private final Listener<String> mListener;

    public StringRequest(@MethodDef int method, String url, Listener<String> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    @Override
    public Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
        String result;
        try {
            result = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
        } catch (UnsupportedEncodingException e) {
            result = new String(networkResponse.data);
        }

        return Response.success(result, HttpHeaderParser.parseResponseHeaders(networkResponse.headers));
    }

    @Override
    public void deliverResponse(String data) {
        if (mListener != null) {
            mListener.onResponse(data);
        }
    }
}
