package com.shoo.volley;

/**
 * Created by Shoo on 17-4-11.
 */
public interface RetryPolicy {

    boolean retry(VolleyError error);

}
