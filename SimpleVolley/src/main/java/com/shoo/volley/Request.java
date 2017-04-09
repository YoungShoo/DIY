package com.shoo.volley;

import android.support.annotation.IntDef;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Shoo on 17-4-9.
 */
public abstract class Request<T> {

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    public interface Method {
        int GET = 1;
        int POST = 2;
        int HEAD = 3;
    }

    @IntDef({Method.GET, Method.POST, Method.HEAD})
    public @interface MethodDef {}

    public interface ErrorListener {
        void deliverError(VolleyError error);
    }

    private @MethodDef int mMethod;
    private String mUrl;
    private ErrorListener mErrorListener;
    private RequestQueue mRequestQueue;
    private Cache.Entry mCacheEntry;
    private boolean mDelivered = false;
    private boolean mCanceled = false;
    private boolean mShouldCache = false;

    public Request(@MethodDef int method, String url, ErrorListener errorListener) {
        mMethod = method;
        mUrl = url;
        mErrorListener = errorListener;
    }

    public void cancel() {
        mCanceled = true;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
    }

    public void finish() {
        mRequestQueue.finish(this);
    }

    public abstract Response<T> parseNetworkResponse(NetworkResponse networkResponse);

    public abstract void deliverResponse(T data);

    public void deliverError(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.deliverError(error);
        }
    }

    public boolean shouldCache() {
        return mShouldCache;
    }

    public void setShouldCache(boolean shouldCache) {
        mShouldCache = shouldCache;
    }

    public String getCacheKey() {
        return getUrl();
    }

    public boolean hasHadResponseDelivered() {
        return mDelivered;
    }

    public void markDelivered() {
        mDelivered = true;
    }

    public Cache.Entry getCacheEntry() {
        return mCacheEntry;
    }

    public void setCacheEntry(Cache.Entry entry) {
        mCacheEntry = entry;
    }

    public Map<String, String> getHeaders() {
        return Collections.EMPTY_MAP;
    }

    public @MethodDef int getMethod() {
        return mMethod;
    }

    public String getUrl() {
        return mUrl;
    }

    public byte[] getBody() {
        return new byte[0];
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }
}
