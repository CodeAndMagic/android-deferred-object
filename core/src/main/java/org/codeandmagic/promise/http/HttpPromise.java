package org.codeandmagic.promise.http;

import android.net.http.AndroidHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.codeandmagic.promise.Pipe;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.http.StatusCodeSelector.RangeStatusCodeSelector;
import org.codeandmagic.promise.impl.AbstractPromise;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by evelina on 26/02/2014.
 */
public class HttpPromise extends AbstractPromise<HttpResponse> {

    public static final StatusCodeSelector STATUS_CODE_SUCCESS = new RangeStatusCodeSelector(200, 299);

    private static final ThreadPoolExecutor DEFAULT_EXECUTOR = new ScheduledThreadPoolExecutor(4);
    private static final HttpClient DEFAULT_CLIENT = AndroidHttpClient.newInstance("Android");

    public HttpPromise(HttpClient client, HttpUriRequest request) {
        this(client, DEFAULT_EXECUTOR, request);
    }

    public HttpPromise(HttpUriRequest request) {
        this(DEFAULT_CLIENT, DEFAULT_EXECUTOR, request);
    }

    public HttpPromise(final HttpClient client, final ThreadPoolExecutor executor, final HttpUriRequest request) {

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    success(client.execute(request));
                } catch (IOException e) {
                    failure(e);
                }
            }
        });
    }

    public static <T> Pipe<T, HttpResponse> asPipe(final HttpUriRequest request) {
        return new Pipe<T, HttpResponse>() {
            @Override
            public Promise<HttpResponse> transform(T value) {
                return new HttpPromise(request);
            }
        };
    }

    public static <T> Pipe<T, HttpResponse> asPipe(final HttpClient client, final HttpUriRequest request) {
        return new Pipe<T, HttpResponse>() {
            @Override
            public Promise<HttpResponse> transform(T value) {
                return new HttpPromise(client, request);
            }
        };
    }
}
