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
package org.codeandmagic.promise;

/**
 * Simplified version of {@link Promise3} which returns a {@link java.lang.Throwable}
 * in case of failure and exposes progress as a Float.
 * <p/>
 * Created by cristian on 10/02/2014.
 */
public interface Promise<Success> extends Promise3<Success, Throwable, Float> {

    @Override
    public Promise<Success> onSuccess(Callback<Success> onSuccess);

    @Override
    public Promise<Success> onFailure(Callback<Throwable> onFailure);

    @Override
    public Promise<Success> onProgress(Callback<Float> onProgress);

    @Override
    public Promise<Success> onComplete(Callback<Either<Throwable, Success>> onComplete);

    @Override
    public <Success2> Promise<Success2> map(final Transformation<Success, Success2> transform);

    @Override
    public Promise<Success> andThen(Callback<Success> onSuccess,
                                    Callback<Throwable> onFailure,
                                    Callback<Float> onProgress);

    @Override
    public Promise<Success> flatRecover(final Transformation<Throwable, Either<Throwable, Success>> transform);

    @Override
    public Promise<Success> recover(final Transformation<Throwable, Success> transform);


    public <Success2> Promise<Success2> pipe(final Pipe<Success, Success2> transform);

    public Promise<Success> recoverWith(final Pipe<Throwable, Success> transform);

    @Override
    public Promise<Success> runOnUiThread();
}
