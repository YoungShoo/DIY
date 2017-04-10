package com.shoo.volley;

import android.support.annotation.IntDef;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Shoo on 17-4-9.
 */
public abstract class Request<T> implements Comparable<Request<T>> {

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    public interface Method {
        int GET = 1;
        int POST = 2;
        int HEAD = 3;
    }

    @IntDef({Method.GET, Method.POST, Method.HEAD})
    public @interface MethodDef {}

    public interface Priority {
        int LOW = 1;
        int NORMAL = 2;
        int HIGH = 3;
        int IMMEDIATE = 4;
    }

    @IntDef({Priority.LOW, Priority.NORMAL, Priority.HIGH, Priority.IMMEDIATE})
    public @interface PriorityDef {}

    public interface Listener<T> {
        void onResponse(T result);
    }

    public interface ErrorListener {
        void onErrorResponse(VolleyError error);
    }

    private @MethodDef int mMethod;
    private String mUrl;
    private ErrorListener mErrorListener;
    private RequestQueue mRequestQueue;
    private Cache.Entry mCacheEntry;
    private boolean mDelivered = false;
    private boolean mCanceled = false;
    private boolean mShouldCache = false;
    private int mPriority = Priority.NORMAL;
    private int mSequence;

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
            mErrorListener.onErrorResponse(error);
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

    public void setPriority(@PriorityDef int priority) {
        mPriority = priority;
    }

    private @PriorityDef int getPriority() {
        return mPriority;
    }

    public void setSequence(int sequence) {
        mSequence = sequence;
    }

    public int getSequence() {
        return mSequence;
    }

    @Override
    public int compareTo(Request<T> that) {
        return this.getPriority() == that.getPriority() ? that.getSequence() - this.getSequence() : that.getPriority
                () - this.getPriority();
    }
}
