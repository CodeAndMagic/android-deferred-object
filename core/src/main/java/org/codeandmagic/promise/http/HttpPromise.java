/*
 * Copyright (c) 2014 CodeAndMagic
 * Cristian Vrabie, Evelina Vrabie
 *
 * This file is part of android-promise.
 * android-promise is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License,or (at your option)
 * any later version.
 *
 * android-promise is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with android-promise. If not, see <http://www.gnu.org/licenses/>.
 */

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
