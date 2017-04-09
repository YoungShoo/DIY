package com.shoo.volley;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Shoo on 17-4-9.
 */

public class RequestQueue {

    private static final int DEFAULT_NETWORK_THREAD_SIZE = 4;
    private final Network mNetwork;
    private final Cache mCache;
    private final ResponseDelivery mDelivery;
    private final NetworkDispatcher[] mNetworkDispatchers;
    private final CacheDispatcher mCacheDispatcher;
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue = new PriorityBlockingQueue<>();
    private final PriorityBlockingQueue<Request<?>> mCacheQueue = new PriorityBlockingQueue<>();
    private final Set<Request<?>> mCurrentRequest = new HashSet<>();
    private final Map<String, Queue<Request<?>>> mWaitingQueue = new HashMap<>();

    public RequestQueue(Network network, Cache cache) {
        mNetwork = network;
        mCache = cache;
        mDelivery = new ExecutorDelivery(new Handler(Looper.getMainLooper()));
        mNetworkDispatchers = new NetworkDispatcher[DEFAULT_NETWORK_THREAD_SIZE];
        mCacheDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue, mCache, mDelivery);
    }

    public void start() {
        stop();
        for (int i = 0; i < mNetworkDispatchers.length; i++) {
            mNetworkDispatchers[i] = new NetworkDispatcher(mNetworkQueue, mNetwork,
                    mCache, mDelivery);
            mNetworkDispatchers[i].start();
        }
        mCacheDispatcher.start();
    }

    public void stop() {
        for (NetworkDispatcher networkDispatcher : mNetworkDispatchers) {
            networkDispatcher.quit();
        }
        mCacheDispatcher.quit();
    }

    public void add(Request<?> request) {
        synchronized (mCurrentRequest) {
            mCurrentRequest.add(request);
        }

        if (!request.shouldCache()) {
            mNetworkQueue.add(request);
            return;
        }

        synchronized (mWaitingQueue) {
            String cacheKey = request.getCacheKey();
            if (mWaitingQueue.containsKey(cacheKey)) {
                Queue<Request<?>> stagedRequests = mWaitingQueue.get(cacheKey);
                if (stagedRequests == null) {
                    stagedRequests = new LinkedList<>();
                    mWaitingQueue.put(cacheKey, stagedRequests);
                }
                stagedRequests.add(request);
            } else {
                mWaitingQueue.put(cacheKey, null);
                mCacheQueue.add(request);
            }
        }
    }

    public void finish(Request<?> request) {
        synchronized (mCurrentRequest) {
            mCurrentRequest.remove(request);
        }

        if (request.shouldCache()) {
            synchronized (mWaitingQueue) {
                Queue<Request<?>> stagedRequests = mWaitingQueue.remove(request.getCacheKey());
                if (stagedRequests != null) {
                    mCacheQueue.addAll(stagedRequests);
                }
            }
        }
    }
}
