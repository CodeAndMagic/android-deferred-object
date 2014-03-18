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
 */package org.codeandmagic.promise.sample.tests;

import android.test.ActivityInstrumentationTestCase2;
import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.impl.DeferredObject;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.sample.PlanetsActivity;

/**
 * Created by evelina on 23/02/2014.
 */
public class RunOnUiThreadTests extends ActivityInstrumentationTestCase2<PlanetsActivity> {

    private String threadName1;
    private String threadName2;

    public RunOnUiThreadTests() {
        super(PlanetsActivity.class);
    }

    public void testRunOnUiThread() {
        final DeferredObject<String> promise = new DeferredObject<String>();
        promise.onSuccess(new Callback<String>() {
            @Override
            public void onCallback(String result) {
                threadName1 = Thread.currentThread().getName();
            }
        });

        final Promise<String> promise2 = promise.runOnUiThread();
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
        assertEquals("main_activity", threadName2);
    }
}
