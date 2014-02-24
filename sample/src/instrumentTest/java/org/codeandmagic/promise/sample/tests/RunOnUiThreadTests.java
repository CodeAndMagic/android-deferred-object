package org.codeandmagic.promise.sample.tests;

import android.test.ActivityInstrumentationTestCase2;
import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.SimpleDeferredObject;
import org.codeandmagic.promise.SimplePromise;
import org.codeandmagic.promise.sample.MainActivity;

/**
 * Created by evelina on 23/02/2014.
 */
public class RunOnUiThreadTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private String threadName1;
    private String threadName2;

    public RunOnUiThreadTests() {
        super(MainActivity.class);
    }

    public void testRunOnUiThread() {
        final SimpleDeferredObject<String> promise = new SimpleDeferredObject<String>();
        promise.onSuccess(new Callback<String>() {
            @Override
            public void onCallback(String result) {
                threadName1 = Thread.currentThread().getName();
            }
        });

        final SimplePromise<String> promise2 = promise.runOnUiThread();
        promise2.onSuccess(new Callback<String>() {
            @Override
            public void onCallback(String result) {
                threadName2 = Thread.currentThread().getName();
            }
        });


        new Thread("Thread1") {
            @Override
            public void run() {
                promise.success("Hello");
            }
        }.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Thread1", threadName1);
        assertEquals("main", threadName2);
    }
}
