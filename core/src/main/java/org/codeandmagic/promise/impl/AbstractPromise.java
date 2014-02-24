/*
 * Copyright (c) 2014 Cristian Vrabie, Evelina Vrabie.
 *
 * This file is part of android-promise.
 * android-promise is free software: you can redistribute it and/or modify it
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

package org.codeandmagic.promise.impl;

import org.codeandmagic.promise.*;

/**
 * Created by cristian on 10/02/2014.
 */
public class AbstractPromise<Success> extends AbstractPromise3<Success, Throwable, Float>
        implements Promise<Success> {

    @SuppressWarnings("unchecked")
    @Override
    protected <S, F, P> AbstractPromise3<S, F, P> newPromise() {
        return (AbstractPromise3<S, F, P>) new AbstractPromise<S>();
    }

    @Override
    public Promise<Success> onSuccess(Callback<Success> onSuccess) {
        return (Promise<Success>) super.onSuccess(onSuccess);
    }

    @Override
    public Promise<Success> onFailure(Callback<Throwable> onFailure) {
        return (Promise<Success>) super.onFailure(onFailure);
    }

    @Override
    public Promise<Success> onProgress(Callback<Float> onProgress) {
        return (Promise<Success>) super.onProgress(onProgress);
    }

    @Override
    public Promise<Success> onComplete(Callback<Either<Throwable, Success>> onComplete) {
        return (Promise<Success>) super.onComplete(onComplete);
    }

    @Override
    public <Success2> Promise<Success2> map(final Transformation<Success, Success2> transform) {
        return (Promise<Success2>) super.map(transform);
    }

    @Override
    public Promise<Success> recover(Transformation<Throwable, Success> transform) {
        return (Promise<Success>) super.recover(transform);
    }

    @Override
    public Promise<Success> flatRecover(Transformation<Throwable, Either<Throwable, Success>> transform) {
        return (Promise<Success>) super.flatRecover(transform);
    }

    @Override
    public Promise<Success> andThen(Callback<Success> onSuccess,
                                          Callback<Throwable> onFailure,
                                          Callback<Float> onProgress) {
        return (Promise<Success>) super.andThen(onSuccess, onFailure, onProgress);
    }

    @Override
    public <Success2> Promise<Success2> pipe(final Pipe<Success, Success2> transform) {

        final AbstractPromise<Success2> promise = new AbstractPromise<Success2>();

        this.onSuccess(new Callback<Success>() {
            @Override
            public void onCallback(Success result) {
                transform.transform(result).onComplete(new Callback<Either<Throwable, Success2>>() {
                    @Override
                    public void onCallback(Either<Throwable, Success2> result2) {
                        promise.complete(result2);
                    }
                });
            }
        }).onFailure(new Callback<Throwable>() {
            @Override
            public void onCallback(Throwable result) {
                promise.failure(result);
            }
        });

        return promise;
    }

    @Override
    public Promise<Success> recoverWith(final Pipe<Throwable, Success> transform) {

        final AbstractPromise<Success> promise = new AbstractPromise<Success>();

        this.onSuccess(new Callback<Success>() {
            @Override
            public void onCallback(Success result) {
                promise.success(result);
            }
        }).onFailure(new Callback<Throwable>() {
            @Override
            public void onCallback(Throwable result) {
                transform.transform(result).onComplete(new Callback<Either<Throwable, Success>>() {
                    @Override
                    public void onCallback(Either<Throwable, Success> result2) {
                        promise.complete(result2);
                    }
                });
            }
        });

        return promise;
    }

    @Override
    public Promise<Success> runOnUiThread() {
        return (Promise<Success>) super.runOnUiThread();
    }
}
