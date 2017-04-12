package com.shoo.volley;

/**
 * Created by Shoo on 17-4-9.
 */
public interface ResponseDelivery {

    void postResponse(Request<?> request, Response<?> response);

    void postResponse(Request<?> request, Response<?> response, Runnable runnable);
}
