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

package org.codeandmagic.promise.impl;

import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.MergeFailure;
import org.codeandmagic.promise.Promise;

import java.lang.reflect.Array;

/**
 * Created by evelina on 10/02/2014.
 */
public class DeferredObject<Success> extends AbstractPromise<Success> {

    public static <S> DeferredObject<S> successful(S value) {
        final DeferredObject<S> deferredObject = new DeferredObject<S>();
        deferredObject.success(value);
        return deferredObject;
    }

    public static <S> DeferredObject<S> failed(Throwable value) {
        final DeferredObject<S> deferredObject = new DeferredObject<S>();
        deferredObject.failure(value);
        return deferredObject;
    }

    public static <T, S extends T> DeferredObject<T[]> merge(Class<T> clazz, Promise<S>... promises) {
        return new MergePromise<T, S>(clazz, 0, promises);
    }

    public static <T, S extends T> DeferredObject<T[]> merge(Class<T> clazz, int allowedFailures, Promise<S>... promises) {
        return new MergePromise<T, S>(clazz, allowedFailures, promises);
    }

    @Override
    public void success(Success resolved) {
        super.success(resolved);
    }

    @Override
    public void failure(Throwable throwable) {
        super.failure(throwable);
    }

    @Override
    public void progress(Float progress) {
        super.progress(progress);
    }

    public static class MergePromise<T, S extends T> extends DeferredObject<T[]> {

        private final Promise<S>[] mPromises;
        private final int mLength;
        private final int mAllowedFailures;

        private final Throwable[] mFailures;
        private final T[] mSuccesses;
        private int mCountCompleted = 0;
        private int mCountFailures = 0;


        private Callback<Throwable> newFailureCallback(final int index) {
            return new Callback<Throwable>() {
                @Override
                public void onCallback(Throwable result) {
                    synchronized (MergePromise.this) {
                        mFailures[index] = result;
                        mCountCompleted++;
                        mCountFailures++;
                        MergePromise.this.progress((float) mCountCompleted);

                        if (mCountFailures > mAllowedFailures) {
                            MergePromise.this.failure(new MergeFailure("Failed MergePromise because more than '"
                                    + mAllowedFailures + "' promises have failed.", mFailures));
                        }
                    }
                }
            };
        }

        private Callback<S> newSuccessCallback(final int index) {
            return new Callback<S>() {
                @Override
                public void onCallback(S result) {
                    synchronized (MergePromise.this) {
                        mSuccesses[index] = result;
                        mCountCompleted++;
                        MergePromise.this.progress((float) mCountCompleted);

                        if (mCountCompleted == mLength) {
                            MergePromise.this.success(mSuccesses);
                        }
                    }
                }
            };
        }

        public MergePromise(Class<T> clazz, int allowedFailures, Promise<S>... promises) {
            if (promises.length < 1) {
                throw new IllegalArgumentException("You need at least one promise.");
            }

            mPromises = promises;
            mLength = promises.length;
            mAllowedFailures = allowedFailures < 0 ? promises.length : allowedFailures;

            mFailures = new Throwable[mLength];
            mSuccesses = (T[]) Array.newInstance(clazz, mLength);

            for (int i = 0; i < mLength; ++i) {
                final Promise<S> promise = promises[i];
                promise.onSuccess(newSuccessCallback(i));
                promise.onFailure(newFailureCallback(i));
            }
        }
    }
}
