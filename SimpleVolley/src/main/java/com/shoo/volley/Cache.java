package com.shoo.volley;

import java.util.Map;

/**
 * Created by Shoo on 17-4-9.
 */
public interface Cache {

    void put(String cacheKey, Entry entry);

    Entry get(String cacheKey);

    class Entry {

        public Map<String, String> responseHeaders;

        public byte[] data;
        public long lastModified;
    }
}
