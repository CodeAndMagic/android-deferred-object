/*
 * Copyright (c) 2014 Cristian Vrabie, Evelina Vrabie.
 *
 * This file is part of android-deferred-object.
 * android-deferred-object is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License,or (at your option)
 * any later version.
 *
 * android-deferred-object is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with android-deferred-object.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.codeandmagic.deferredobject.tests;

import junit.framework.TestCase;
import org.codeandmagic.deferredobject.*;

import static org.mockito.Mockito.*;

/**
 * Created by cristian on 10/02/2014.
 */
public class TransformationTests extends TestCase {

    private MapTransformation<Integer, String> intToString = new MapTransformation<Integer, String>() {
        @Override
        public String transform(Integer success) {
            return (success * 2) + "!";
        }
    };

    private FlatMapTransformation<String, Integer, Throwable> stringToInt = new FlatMapTransformation<String, Integer, Throwable>() {
        @Override
        public Either<Throwable, Integer> transform(String value) {
            try {
                return new Right<Throwable, Integer>(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                return new Left<Throwable, Integer>(e);
            }
        }
    };

    public void testMapSuccess() {
        InspectableDeferredObject<Integer, Void, Void> promise = new InspectableDeferredObject<Integer, Void, Void>();
        Promise<String, Void, Void> promise2 = promise.map(intToString);

        Callback<String> callback = mock(Callback.class);
        promise2.onSuccess(callback);

        assertFalse(promise2.isSuccess());

        promise.success(1);

        assertTrue(promise2.isSuccess());
        verify(callback, only()).onCallback("2!");
    }

    public void testMapFailure() {
        InspectableDeferredObject<Integer, Throwable, Void> promise = new InspectableDeferredObject<Integer, Throwable, Void>();
        Promise<String, Throwable, Void> promise2 = promise.map(intToString);

        Callback<String> onSuccess = mock(Callback.class);
        Callback<Throwable> onFailure = mock(Callback.class);
        Callback<Either<Throwable, String>> onComplete = mock(Callback.class);
        promise2.onSuccess(onSuccess).onFailure(onFailure).onComplete(onComplete);

        assertFalse(promise2.isSuccess());

        promise.failure(new Exception());

        assertFalse(promise2.isSuccess());
        verify(onSuccess, never()).onCallback(anyString());
        verify(onFailure, only()).onCallback(any(Throwable.class));
        verify(onComplete, only()).onCallback(any(Either.class));
        assertTrue(promise2.isFailure());
    }

    public void testFlatMap() {
        InspectableDeferredObject<String, Throwable, Void> promise = new InspectableDeferredObject<String, Throwable, Void>();
        Promise<Integer, Throwable, Void> promise2 = promise.flatMap(stringToInt);

        Callback<Integer> onSuccess = mock(Callback.class);
        Callback<Throwable> onFailure = mock(Callback.class);
        Callback<Either<Throwable, Integer>> onComplete = mock(Callback.class);
        promise2.onSuccess(onSuccess).onFailure(onFailure).onComplete(onComplete);

        assertFalse(promise2.isSuccess());

        // 'abc' is not convertible to Integer
        promise.success("abc");

        assertFalse(promise2.isSuccess());
        verify(onSuccess, never()).onCallback(anyInt());
        verify(onFailure, only()).onCallback(any(Throwable.class));
        verify(onComplete, only()).onCallback(any(Either.class));
        assertTrue(promise2.isFailure());
    }
}
