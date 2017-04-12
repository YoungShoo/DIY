package com.shoo.volley;

import android.os.Process;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Shoo on 17-4-9.
 */
public class CacheDispatcher extends Thread {

    private final BlockingQueue<Request<?>> mCacheQueue;
    private final BlockingQueue<Request<?>> mNetworkQueue;
    private final Cache mCache;
    private final ResponseDelivery mDelivery;
    private boolean mQuit;

    public CacheDispatcher(BlockingQueue<Request<?>> cacheQueue, BlockingQueue<Request<?>>
            networkQueue, Cache cache, ResponseDelivery delivery) {
        mCacheQueue = cacheQueue;
        mNetworkQueue = networkQueue;
        mCache = cache;
        mDelivery = delivery;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        for (;;) {
            try {
                final Request<?> request = mCacheQueue.take();

                if (request.isCanceled()) {
                    request.finish();
                    continue;
                }

                Cache.Entry entry = mCache.get(request.getCacheKey());

                if (entry == null) {
                    mNetworkQueue.add(request);
                    continue;
                }

                if (entry.isExpired()) {
                    request.setCacheEntry(entry);
                    mNetworkQueue.add(request);
                    continue;
                }

                Response<?> response = request.parseNetworkResponse(new NetworkResponse(entry.data, entry
                        .responseHeaders));

                if (!entry.refreshNeeded()) {
                    mDelivery.postResponse(request, response);
                } else {
                    request.setCacheEntry(entry);
                    response.intermediate = true;
                    mDelivery.postResponse(request, response, new Runnable() {
                        @Override
                        public void run() {
                            mNetworkQueue.add(request);
                        }
                    });
                }
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
            }
        }
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }
}
