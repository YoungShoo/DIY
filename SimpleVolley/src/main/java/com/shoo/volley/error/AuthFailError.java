package com.shoo.volley.error;

import com.shoo.volley.NetworkResponse;
import com.shoo.volley.VolleyError;

/**
 * Created by Shoo on 17-4-11.
 */
public class AuthFailError extends VolleyError {

    public AuthFailError(NetworkResponse networkResponse) {
        super(networkResponse);
    }
}
