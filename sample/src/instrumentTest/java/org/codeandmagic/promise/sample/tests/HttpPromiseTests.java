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

package org.codeandmagic.promise.sample.tests;

import android.test.ActivityInstrumentationTestCase2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.http.HttpPromise;
import org.codeandmagic.promise.sample.PlanetsActivity;

import static org.mockito.Mockito.*;

/**
 * Created by evelina on 26/02/2014.
 */

public class HttpPromiseTests extends ActivityInstrumentationTestCase2<PlanetsActivity> {

    public HttpPromiseTests() {
        super(PlanetsActivity.class);
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
