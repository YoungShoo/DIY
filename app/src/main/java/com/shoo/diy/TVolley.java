package com.shoo.diy;

import android.app.Activity;

import com.shoo.volley.RequestQueue;
import com.shoo.volley.Volley;

/**
 * Created by Shoo on 17-4-10.
 */

public class TVolley {

    public static void test(Activity activity) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity, null);
//        requestQueue.add();
    }
}
