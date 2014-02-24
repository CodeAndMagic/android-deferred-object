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

package org.codeandmagic.deferredobject;

import java.lang.reflect.Array;

/**
 * Created by evelina on 10/02/2014.
 */
public class SimpleDeferredObject<Success> extends AbstractSimplePromise<Success> {

    public static <S> SimpleDeferredObject<S> successful(S value) {
        final SimpleDeferredObject<S> deferredObject = new SimpleDeferredObject<S>();
        deferredObject.success(value);
        return deferredObject;
    }

    public static <S> SimpleDeferredObject<S> failed(Throwable value) {
        final SimpleDeferredObject<S> deferredObject = new SimpleDeferredObject<S>();
        deferredObject.failure(value);
        return deferredObject;
    }

    public static <T, S extends T> SimpleDeferredObject<T[]> merge(Class<T> clazz, SimplePromise<S>... promises) {
        return new MergePromise<T, S>(clazz, 0, promises);
    }

    public static <T, S extends T> SimpleDeferredObject<T[]> merge(Class<T> clazz, int allowedFailures, SimplePromise<S>... promises) {
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

    public static class MergePromise<T, S extends T> extends SimpleDeferredObject<T[]> {

        private final SimplePromise<S>[] promises;
        private final int length;
        private final int allowedFailures;

        private final Throwable[] failures;
        private final T[] successes;
        private int countCompleted = 0;
        private int countFailures = 0;


        private Callback<Throwable> newFailureCallback(final int index) {
            return new Callback<Throwable>() {
                @Override
                public void onCallback(Throwable result) {
                    synchronized (MergePromise.this) {
                        failures[index] = result;
                        countCompleted++;
                        countFailures++;
                        MergePromise.this.progress((float) countCompleted);

                        if (countFailures > allowedFailures) {
                            MergePromise.this.failure(new MergePromiseFailure("Failed MergePromise because more than '"
                                    + allowedFailures + "' promises have failed.", failures));
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
                        successes[index] = result;
                        countCompleted++;
                        MergePromise.this.progress((float) countCompleted);

                        if (countCompleted == length) {
                            MergePromise.this.success(successes);
                        }
                    }
                }
            };
        }

        public MergePromise(Class<T> clazz, int allowedFailures, SimplePromise<S>... promises) {
            if (promises.length < 1) {
                throw new IllegalArgumentException("You need at least one promise.");
            }

            this.promises = promises;
            this.length = promises.length;
            this.allowedFailures = allowedFailures < 0 ? promises.length : allowedFailures;

            this.failures = new Throwable[length];
            this.successes = (T[]) Array.newInstance(clazz, length);

            for (int i = 0; i < length; ++i) {
                final SimplePromise<S> promise = promises[i];
                promise.onSuccess(newSuccessCallback(i));
                promise.onFailure(newFailureCallback(i));
            }
        }
    }
}
