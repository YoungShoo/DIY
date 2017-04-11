package com.shoo.volley;

import android.util.Log;

/**
 * Created by Shoo on 17-4-11.
 */

public class DefaultRetryPolicy implements RetryPolicy {

    private static final String TAG = "DefaultRetryPolicy";

    public static final int DEFAULT_MAX_RETRY_COUNT = 1;

    public static final long DEFAULT_TIMEOUT_MS = 2500;

    private int mMaxRetryCount = DEFAULT_MAX_RETRY_COUNT;

    private long mTimeoutMs = DEFAULT_TIMEOUT_MS;

    private int mCurrentRetryCount = 0;

    public void setMaxRetryCount(int retryCount) {
        mMaxRetryCount = retryCount;
    }

    public void setTimeoutMs(long timeoutMs) {
        mTimeoutMs = timeoutMs;
    }

    @Override
    public boolean retry(VolleyError error) {
        long networkTimeMs = error.networkResponse != null ? error.networkResponse.networkTimeMs : -1;
        Log.d(TAG, "retry: error = " + error + ", mCurrentRetryCount = " + mCurrentRetryCount + ", networkTimeMs = "
                + networkTimeMs);
        if (mCurrentRetryCount++ < mMaxRetryCount) {
            return true;
        }
        return networkTimeMs != -1 && networkTimeMs < mTimeoutMs;
    }
}
