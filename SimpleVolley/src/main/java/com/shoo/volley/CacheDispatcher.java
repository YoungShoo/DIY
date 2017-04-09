package com.shoo.volley;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Shoo on 17-4-9.
 */
public class CacheDispatcher extends Thread {

    private boolean mQuit;

    public CacheDispatcher(BlockingQueue<Request<?>> cacheQueue, BlockingQueue<Request<?>>
            networkQueue, Cache cache, ResponseDelivery delivery) {

    }

    @Override
    public void run() {
        super.run();
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }
}
