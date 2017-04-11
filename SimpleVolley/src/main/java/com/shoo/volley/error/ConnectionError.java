package com.shoo.volley.error;

import com.shoo.volley.VolleyError;

/**
 * Created by Shoo on 17-4-11.
 */
public class ConnectionError extends VolleyError {

    public ConnectionError(Exception e) {
        super(e);
    }
}
