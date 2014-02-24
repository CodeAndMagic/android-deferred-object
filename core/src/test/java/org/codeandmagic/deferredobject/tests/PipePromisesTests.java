package org.codeandmagic.deferredobject.tests;

import org.codeandmagic.deferredobject.*;
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


    public SimplePromise<Integer> deferredInt(final int number) {
        final SimpleDeferredObject<Integer> deferredObject = new SimpleDeferredObject<Integer>();

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

    public SimplePromise<Integer> deferredException() {
        final SimpleDeferredObject<Integer> deferredObject = new SimpleDeferredObject<Integer>();

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

        DeferredObject.<Integer, Throwable, Void>successful(3).pipe(new PipeTransformation<Integer, String, Throwable>() {
            @Override
            public Promise<String, Throwable, Void> transform(Integer value) {
                return DeferredObject.<String, Throwable, Void>failed(new Exception("Failed"));
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
        SimplePromise<Integer> exception = deferredException().onSuccess(onSuccess1).onFailure(onFailure1);
        SimplePromise<Integer> number = exception.recoverWith(new SimplePipeTransformation<Throwable, Integer>() {
            @Override
            public SimplePromise<Integer> transform(Throwable value) {
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
