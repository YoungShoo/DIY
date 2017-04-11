package com.shoo.volley.error;

import com.shoo.volley.VolleyError;

/**
 * Created by Shoo on 17-4-11.
 */

public class NetworkError extends VolleyError {

    public NetworkError(Exception e) {
        super(e);
    }
}
