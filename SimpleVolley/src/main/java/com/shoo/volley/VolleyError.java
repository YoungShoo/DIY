package com.shoo.volley;

/**
 * Created by Shoo on 17-4-9.
 */
public class VolleyError extends Exception {

    public final NetworkResponse networkResponse;
    private long mNetworkTimeMs;

    public VolleyError(Exception e) {
        super(e);
        networkResponse = null;
    }

    public VolleyError(NetworkResponse networkResponse) {
        this.networkResponse = networkResponse;
    }

    public void setNetworkTimeMs(long timeMs) {
        mNetworkTimeMs = timeMs;
    }
}
