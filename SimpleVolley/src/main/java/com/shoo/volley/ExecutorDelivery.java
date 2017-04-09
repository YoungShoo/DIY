package com.shoo.volley;

import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * Created by Shoo on 17-4-9.
 */
public class ExecutorDelivery implements ResponseDelivery {

    private final Executor mResponsePoster;

    public ExecutorDelivery(final Handler handler) {
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable runnable) {
                handler.post(runnable);
            }
        };
    }

    @Override
    public void postResponse(Request<?> request, Response<?> response) {
        request.markDelivered();
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response));
    }

    private static class ResponseDeliveryRunnable implements Runnable {

        private final Request mRequest;
        private final Response mResponse;

        public ResponseDeliveryRunnable(Request request, Response response) {
            mRequest = request;
            mResponse = response;
        }

        @Override
        public void run() {
            if (mRequest.isCanceled()) {
                mRequest.finish();
                return;
            }

            if (mResponse.isSuccess()) {
                mRequest.deliverResponse(mResponse.data);
            } else {
                mRequest.deliverError(mResponse.error);
            }
        }
    }
}
