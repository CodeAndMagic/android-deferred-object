package org.codeandmagic.promise.tests;

import org.codeandmagic.promise.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by evelina on 11/02/2014.
 */
@RunWith(JUnit4.class)
public class SimplePromiseTests {

    private MapTransformation<Integer, String> intToString = new MapTransformation<Integer, String>() {
        @Override
        public String transform(Integer success) {
            return (success * 2) + "!";
        }
    };

    private MapTransformation<Throwable, String> throwableToString = new MapTransformation<Throwable, String>() {
        @Override
        public String transform(Throwable value) {
            return value.getMessage();
        }
    };

    @Test
    public void testMapSuccess() {
        Callback<String> callback = mock(Callback.class);
        SimpleDeferredObject<Integer> promise = new SimpleDeferredObject<Integer>();
        SimplePromise<String> promise2 = promise.map(intToString).onSuccess(callback);

        assertFalse(promise2.isSuccess());

        promise.success(10);

        assertTrue(promise2.isSuccess());
        verify(callback, only()).onCallback("20!");
    }

    @Test
    public void testPromiseGeneralisation() throws Exception {
        Callback<String> onSuccess = mock(Callback.class);
        Callback<String> onFailure = mock(Callback.class);
        SimpleDeferredObject<Integer> promise = new SimpleDeferredObject<Integer>();
        Promise<String, String, Float> promise2 = promise
                .map(intToString, throwableToString)
                .onSuccess(onSuccess)
                .onFailure(onFailure);

        assertFalse(promise2.isFailure());

        promise.failure(new Exception("Wrong"));

        assertTrue(promise2.isFailure());
        verify(onSuccess, never()).onCallback(anyString());
        verify(onFailure, only()).onCallback("Wrong");
    }
}
