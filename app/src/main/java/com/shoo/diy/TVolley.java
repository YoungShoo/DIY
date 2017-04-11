package com.shoo.diy;

import android.app.Activity;
import android.util.Log;

import com.shoo.volley.DefaultRetryPolicy;
import com.shoo.volley.Request;
import com.shoo.volley.RequestQueue;
import com.shoo.volley.StringRequest;
import com.shoo.volley.Volley;
import com.shoo.volley.VolleyError;


/**
 * Created by Shoo on 17-4-10.
 */

public class TVolley {

    private static final String TAG = "TVolley";

    public static void test(Activity activity) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.baidu.com",
                new Request.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Log.d(TAG, "onResponse: result = " + result);
                    }
                },
                new Request.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: error = " + error);
                    }
                });
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy();
        retryPolicy.setMaxRetryCount(3);
        retryPolicy.setTimeoutMs(1000);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }
}
