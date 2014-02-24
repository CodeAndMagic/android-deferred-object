package org.codeandmagic.promise.tests;

import org.codeandmagic.promise.*;
import org.codeandmagic.promise.DeferredObject;
import org.codeandmagic.promise.Pipe;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.Pipe3;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by cristian on 11/02/2014.
 */
@RunWith(JUnit4.class)
public class PipePromisesTests {


    public Promise<Integer> deferredInt(final int number) {
        final DeferredObject<Integer> deferredObject = new DeferredObject<Integer>();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    deferredObject.success(number);
                } catch (InterruptedException e) {
                    deferredObject.failure(e);
                }
            }
        }.start();

        return deferredObject;
    }

    public Promise<Integer> deferredException() {
        final DeferredObject<Integer> deferredObject = new DeferredObject<Integer>();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    deferredObject.failure(new Exception("Failed!"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return deferredObject;
    }


    @Test
    public void testPipe() {
        Callback<String> onSuccess = mock(Callback.class);
        Callback<Throwable> onFailure = mock(Callback.class);

        DeferredObject3.<Integer, Throwable, Void>successful(3).pipe(new Pipe3<Integer, String, Throwable>() {
            @Override
            public Promise3<String, Throwable, Void> transform(Integer value) {
                return DeferredObject3.<String, Throwable, Void>failed(new Exception("Failed"));
            }
        }).onSuccess(onSuccess).onFailure(onFailure);

        verify(onSuccess, never()).onCallback(anyString());
        verify(onFailure, only()).onCallback(any(Exception.class));
    }

    @Test
    @Ignore
    public void testPipeMultiThreaded() {
        Callback<Integer> onSuccess1 = mock(Callback.class);
        Callback<Throwable> onFailure1 = mock(Callback.class);
        Callback<Integer> onSuccess2 = mock(Callback.class);
        Callback<Throwable> onFailure2 = mock(Callback.class);

        long start = currentTimeMillis();
        Promise<Integer> exception = deferredException().onSuccess(onSuccess1).onFailure(onFailure1);
        Promise<Integer> number = exception.recoverWith(new Pipe<Throwable, Integer>() {
            @Override
            public Promise<Integer> transform(Throwable value) {
                return deferredInt(55);
            }
        }).onSuccess(onSuccess2).onFailure(onFailure2);

        // Assert that the promise is returned right away
        assertTrue(currentTimeMillis() - start < 1000);
        assertTrue(exception.isPending());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(onSuccess1, never()).onCallback(anyInt());
        verify(onFailure1, only()).onCallback(any(Exception.class));

        verify(onSuccess2, only()).onCallback(anyInt());
        verify(onFailure2, never()).onCallback(any(Exception.class));

        assertTrue(number.isSuccess());
    }
}
