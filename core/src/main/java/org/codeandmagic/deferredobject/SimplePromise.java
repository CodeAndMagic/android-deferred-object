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

/**
 * Simplified version of {@link org.codeandmagic.deferredobject.Promise} which returns a {@link java.lang.Throwable}
 * in case of failure and exposes progress as a Float.
 * <p/>
 * Created by cristian on 10/02/2014.
 */
public interface SimplePromise<Success> extends Promise<Success, Throwable, Float> {

    @Override
    public SimplePromise<Success> onSuccess(Callback<Success> onSuccess);

    @Override
    public SimplePromise<Success> onFailure(Callback<Throwable> onFailure);

    @Override
    public SimplePromise<Success> onProgress(Callback<Float> onProgress);

    @Override
    public SimplePromise<Success> onComplete(Callback<Either<Throwable, Success>> onComplete);

    @Override
    public <Success2> SimplePromise<Success2> map(final MapTransformation<Success, Success2> transform);

    @Override
    public SimplePromise<Success> andThen(Callback<Success> onSuccess,
                                          Callback<Throwable> onFailure,
                                          Callback<Float> onProgress);

    @Override
    public SimplePromise<Success> recover(final EitherMapTransformation<Throwable, Success, Throwable> transform);

    @Override
    public SimplePromise<Success> recover(final MapTransformation<Throwable, Success> transform);


    public <Success2> SimplePromise<Success2> pipe(final SimplePipeTransformation<Success, Success2> transform);

    public SimplePromise<Success> recoverWith(final SimplePipeTransformation<Throwable, Success> transform);

    @Override
    public SimplePromise<Success> runOnUiThread();
}
