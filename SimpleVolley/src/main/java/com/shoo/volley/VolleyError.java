package com.shoo.volley;

/**
 * Created by Shoo on 17-4-9.
 */
public class VolleyError extends Exception {

    private long mNetworkTimeMs;

    public VolleyError(Exception e) {
        super(e);
    }

    public void setNetworkTimeMs(long timeMs) {
        mNetworkTimeMs = timeMs;
    }
}
