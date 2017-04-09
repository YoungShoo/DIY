package com.shoo.volley;

/**
 * Created by Shoo on 17-4-9.
 */
public interface Network {

    NetworkResponse performRequest(Request<?> request) throws VolleyError;

}
