package com.shoo.volley;

/**
 * Created by Shoo on 17-4-10.
 */
public class NoCache implements Cache {

    @Override
    public void put(String cacheKey, Entry entry) {

    }

    @Override
    public Entry get(String cacheKey) {
        return new Entry();
    }
}
