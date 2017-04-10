package com.shoo.volley;

import android.content.Context;

/**
 * Created by Shoo on 17-4-10.
 */

public class Volley {

    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        Cache cache = new NoCache();

        if (stack == null) {
            stack = new HurlStack();
        }
        Network network = new BasicNetwork(stack);

        RequestQueue requestQueue = new RequestQueue(network, cache);
        requestQueue.start();

        return requestQueue;
    }

}
