package com.shoo.volley.error;

import com.shoo.volley.NetworkResponse;
import com.shoo.volley.VolleyError;

/**
 * Created by Shoo on 17-4-11.
 */
public class ServerError extends VolleyError {

    public ServerError(Exception e) {
        super(e);
    }

    public ServerError(NetworkResponse networkResponse) {
        super(networkResponse);
    }
}
