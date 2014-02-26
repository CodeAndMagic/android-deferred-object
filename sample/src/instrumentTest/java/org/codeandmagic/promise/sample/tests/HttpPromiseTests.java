package org.codeandmagic.promise.sample.tests;

import android.test.ActivityInstrumentationTestCase2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.http.HttpPromise;
import org.codeandmagic.promise.sample.MainActivity;

import static org.mockito.Mockito.*;

/**
 * Created by evelina on 26/02/2014.
 */

public class HttpPromiseTests extends ActivityInstrumentationTestCase2<MainActivity> {

    public HttpPromiseTests() {
        super(MainActivity.class);
    }

    public void testHttpGet() {

        Callback<HttpResponse> successCallback = mock(Callback.class);
        Callback<Throwable> failureCallback = mock(Callback.class);
        Callback<Throwable> firstFailureCallback = mock(Callback.class);

        // Make a call that fails
        // Filter for success results
        // If it fails, recover with another call
        new HttpPromise(new HttpGet("http://github.com/not_found"))
                .flatMap(HttpPromise.STATUS_CODE_SUCCESS)
                .onFailure(firstFailureCallback)
                .recoverWith(HttpPromise.<Throwable>asPipe(new HttpGet("http://example.com/")))
                .onSuccess(successCallback)
                .onFailure(failureCallback);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(firstFailureCallback, only()).onCallback(any(Throwable.class));
        verify(successCallback, only()).onCallback(any(HttpResponse.class));
        verify(failureCallback, never()).onCallback(any(Throwable.class));
    }
}
