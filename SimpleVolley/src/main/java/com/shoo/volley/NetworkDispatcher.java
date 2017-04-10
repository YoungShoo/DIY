package com.shoo.volley;

import android.os.Process;
import android.os.SystemClock;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Shoo on 17-4-9.
 */
public class NetworkDispatcher extends Thread {


    private final BlockingQueue<Request<?>> mNetworkQueue;
    private final Network mNetwork;
    private final Cache mCache;
    private ResponseDelivery mDelivery;
    private boolean mQuit = false;

    public NetworkDispatcher(BlockingQueue<Request<?>> networkQueue, Network network, Cache
            cache, ResponseDelivery delivery) {
        mNetworkQueue = networkQueue;
        mNetwork = network;
        mCache = cache;
        mDelivery = delivery;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        for (;;) {
            long startTimeMs = SystemClock.elapsedRealtime();
            Request<?> request;

            try {
                request = mNetworkQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                if (request.isCanceled()) {
                    request.finish();
                    continue;
                }

                NetworkResponse networkResponse = mNetwork.performRequest(request);

                if (networkResponse.notModified && request.hasHadResponseDelivered()) {
                    request.finish();
                }

                Response<?> response = request.parseNetworkResponse(networkResponse);

                String cacheKey = request.getCacheKey();
                if (request.shouldCache() && response.entry != null) {
                    mCache.put(cacheKey, response.entry);
                }

                request.markDelivered();
                mDelivery.postResponse(request, response);
            } catch (VolleyError e) {
                e.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
                mDelivery.postResponse(request, Response.error(e));
            } catch (Exception e) {
                VolleyError error = new VolleyError(e);
                error.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
                mDelivery.postResponse(request, Response.error(error));
            }

        }

    }

    public void quit() {
        mQuit = true;
        interrupt();
    }
}
