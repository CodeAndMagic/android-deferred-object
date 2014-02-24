/*
 * Copyright (c) 2014 Cristian Vrabie, Evelina Vrabie.
 *
 * This file is part of android-promise.
 * android-deferred-object is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License,or (at your option)
 * any later version.
 *
 * android-promise is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with android-promise
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.codeandmagic.promise.tests;

import org.codeandmagic.promise.*;
import org.codeandmagic.promise.impl.DeferredObject;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.Transformation;
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

    private Transformation<Integer, String> intToString = new Transformation<Integer, String>() {
        @Override
        public String transform(Integer success) {
            return (success * 2) + "!";
        }
    };

    private Transformation<Throwable, String> throwableToString = new Transformation<Throwable, String>() {
        @Override
        public String transform(Throwable value) {
            return value.getMessage();
        }
    };

    @Test
    public void testMapSuccess() {
        Callback<String> callback = mock(Callback.class);
        DeferredObject<Integer> promise = new DeferredObject<Integer>();
        Promise<String> promise2 = promise.map(intToString).onSuccess(callback);

        assertFalse(promise2.isSuccess());

        promise.success(10);

        assertTrue(promise2.isSuccess());
        verify(callback, only()).onCallback("20!");
    }

    @Test
    public void testPromiseGeneralisation() throws Exception {
        Callback<String> onSuccess = mock(Callback.class);
        Callback<String> onFailure = mock(Callback.class);
        DeferredObject<Integer> promise = new DeferredObject<Integer>();
        Promise3<String, String, Float> promise2 = promise
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
