package org.codeandmagic.promise.tests;

import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.DeferredObject;
import org.codeandmagic.promise.MergePromiseFailure;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.Promise3.State;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by cristian on 20/02/2014.
 */
@RunWith(JUnit4.class)
public class MergePromiseTests {

    @Test
    public void testMergePromiseSuccess() {
        DeferredObject<Integer> p1 = new DeferredObject<Integer>();
        DeferredObject<Integer> p2 = new DeferredObject<Integer>();
        DeferredObject<Integer> p3 = new DeferredObject<Integer>();

        Callback<Integer[]> onSuccess = mock(Callback.class);
        Callback<Throwable> onFailure = mock(Callback.class);
        Callback<Float> onProgress = mock(Callback.class);

        Promise<Integer[]> merged = DeferredObject.merge(Integer.class, p1, p2, p3)
                .onSuccess(onSuccess)
                .onFailure(onFailure)
                .onProgress(onProgress);

        assertTrue(merged.isPending());

        p1.success(1);

        assertTrue(merged.isPending());
        verify(onProgress, only()).onCallback(1f);

        p3.success(3);
        p2.success(2);

        assertTrue(merged.isSuccess());
        verify(onSuccess, only()).onCallback(new Integer[]{1, 2, 3});
        verify(onFailure, never()).onCallback(any(Throwable.class));
        verify(onProgress, times(3)).onCallback(anyFloat());
    }

    @Test
    public void testMergePromiseNoFailuresAllowed() {
        DeferredObject<Integer> p1 = new DeferredObject<Integer>();
        DeferredObject<Integer> p2 = new DeferredObject<Integer>();
        DeferredObject<Integer> p3 = new DeferredObject<Integer>();

        Callback<Integer[]> onSuccess = mock(Callback.class);
        Callback<Throwable> onFailure = mock(Callback.class);
        Callback<Float> onProgress = mock(Callback.class);

        Promise<Integer[]> merged = DeferredObject.merge(Integer.class, p1, p2, p3)
                .onSuccess(onSuccess)
                .onFailure(onFailure)
                .onProgress(onProgress);

        assertTrue(merged.isPending());

        p1.success(1);

        assertTrue(merged.isPending());
        verify(onProgress, only()).onCallback(1f);

        p3.failure(new Exception());

        assertEquals(State.FAILED, merged.state());

        try {
            p2.success(2);
        } catch (Exception e) {
            // This would normally happen on a different thread so the exception would not be visible
        }

        verify(onSuccess, never()).onCallback(any(Integer[].class));
        verify(onFailure, only()).onCallback(any(MergePromiseFailure.class));
        verify(onProgress, times(2)).onCallback(anyFloat());
    }


    @Test
    public void testMergePromiseTwoFailuresAllowed() {
        DeferredObject<Integer> p1 = new DeferredObject<Integer>();
        DeferredObject<Integer> p2 = new DeferredObject<Integer>();
        DeferredObject<Integer> p3 = new DeferredObject<Integer>();

        Callback<Integer[]> onSuccess = mock(Callback.class);
        Callback<Throwable> onFailure = mock(Callback.class);
        Callback<Float> onProgress = mock(Callback.class);

        Promise<Integer[]> merged = DeferredObject.merge(Integer.class, 2, p1, p2, p3)
                .onSuccess(onSuccess)
                .onFailure(onFailure)
                .onProgress(onProgress);

        assertTrue(merged.isPending());

        p1.failure(new Exception());

        assertTrue(merged.isPending());
        verify(onProgress, only()).onCallback(1f);

        p3.failure(new Exception());

        assertEquals(State.PENDING, merged.state());

        p2.success(2);

        assertTrue(merged.isSuccess());
        verify(onSuccess, only()).onCallback(new Integer[]{null, 2, null});
        verify(onFailure, never()).onCallback(any(Throwable.class));
        verify(onProgress, times(3)).onCallback(anyFloat());
    }
}
