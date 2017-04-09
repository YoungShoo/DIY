package com.shoo.volley;

/**
 * Created by Shoo on 17-4-9.
 */
public class Response<T> {

    public static <T> Response<T> success(T data, Cache.Entry entry) {
        return new Response<>(data, entry, null);
    }

    public static <T> Response<T> error(VolleyError error) {
        return new Response<>(null, null, error);
    }

    public final T data;
    public final Cache.Entry entry;
    public final VolleyError error;

    private Response(T data, Cache.Entry entry, VolleyError error) {
        this.data = data;
        this.entry = entry;
        this.error = error;
    }

    public boolean isSuccess() {
        return error == null;
    }
}
