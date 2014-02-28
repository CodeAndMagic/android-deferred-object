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
 * A Promise3 is an immutable contract for an operation that might complete in the future.
 * The operation can have a successful outcome or it might fail with a specific result.
 * It may also send notifications about the progress of the operation.
 * <p/>
 * A direct result <b>cannot</b> be extracted from a Promise3,
 * instead you should register {@link org.codeandmagic.promise.Callback}s for success, failure or progress.
 * <p/>
 * {@link Transformation}s and
 * {@link Pipe3}s can be applied to a Promise3 to obtain a new one
 * that filters or changes the result or failure.
 * <p/>
 * User: cvrabie1 Date: 09/07/2012
 */
public interface Promise3<Success, Failure, Progress> {

    public enum State {
        PENDING, FAILED, SUCCESS
    }

    public State state();

    public boolean isPending();

    public boolean isFailure();

    public boolean isSuccess();

    /* -------------------------------------------------------------------------------------- */
    // Completion handlers
    /* -------------------------------------------------------------------------------------- */

    /**
     * Called when the operation completes, either successfully or with an exception.
     *
     * @param onComplete
     * @return
     */
    public Promise3<Success, Failure, Progress> onComplete(final Callback<Either<Failure, Success>> onComplete);

    /**
     * Called when the operation completes with success.
     *
     * @param onSuccess
     * @return
     */
    public Promise3<Success, Failure, Progress> onSuccess(final Callback<Success> onSuccess);

    /**
     * Called when the operation completes with a failure.
     *
     * @param onFailure
     * @return
     */
    public Promise3<Success, Failure, Progress> onFailure(final Callback<Failure> onFailure);

    /**
     * Called when there is a progress update on the operation.
     *
     * @param onProgress
     * @return
     */
    public Promise3<Success, Failure, Progress> onProgress(final Callback<Progress> onProgress);


    /**
     * Registers callbacks for success, failure and progress. The Promise3 is left unchanged and can be
     * chained.
     *
     * @param onSuccess
     * @param onFailure
     * @param onProgress
     * @return
     */
    public Promise3<Success, Failure, Progress> andThen(Callback<Success> onSuccess,
                                                       Callback<Failure> onFailure,
                                                       Callback<Progress> onProgress);

    /* -------------------------------------------------------------------------------------- */
    // Transformations
    /* -------------------------------------------------------------------------------------- */

    /**
     * Creates a new Promise3 by applying a {@link Transformation} to
     * the success result of this Promise3.
     * <p/>
     * By contrast to {@link #flatMap(Transformation)}, this cannot transform a successful result into
     * a failure.
     *
     * @param transform  transforms Success into Success2
     * @param <Success2>
     * @return
     */
    public <Success2> Promise3<Success2, Failure, Progress> map(final Transformation<Success, Success2> transform);

    /**
     * Creates a new Promise3 by applying {@link Transformation}s to the success and
     * failure results of this Promise3.
     *
     * @param transformSuccess transformation between success types or null if you want it unchanged
     * @param transformFailure transformation between failure types or null if you want it unchanged
     * @param <Success2>
     * @param <Failure2>
     * @return
     */
    public <Success2, Failure2> Promise3<Success2, Failure2, Progress>
    map(final Transformation<Success, Success2> transformSuccess,
        final Transformation<Failure, Failure2> transformFailure);

    /**
     * Creates a new Promise3 by applying {@link Transformation}s to the success, failure
     * and progress results of this Promise3.
     *
     * @param transformSuccess  transformation between success types or null if you want it unchanged
     * @param transformFailure  transformation between failure types or null if you want it unchanged
     * @param transformProgress transformation between progress types or null if you want it unchanged
     * @param <Success2>
     * @param <Failure2>
     * @param <Progress2>
     * @return
     */
    public <Success2, Failure2, Progress2> Promise3<Success2, Failure2, Progress2>
    map(final Transformation<Success, Success2> transformSuccess,
        final Transformation<Failure, Failure2> transformFailure,
        final Transformation<Progress, Progress2> transformProgress);

    /**
     * Creates a new Promise3 by transforming a successful result into either another type of successful result
     * or a failure result.
     * <p/>
     * Example: a successful HTTP call can be transformed into a failed one if the JSON de-serialisation (expressed
     * as a {@link org.codeandmagic.promise.Transformation}) throws an error.
     *
     * @param transform
     * @return
     */
    public <Success2> Promise3<Success2, Failure, Progress> flatMap(final Transformation<Success, Either<Failure, Success2>> transform);

    /**
     * Creates a new Promise3 by applying a {@link Transformation} from failure to success.
     * If the Promise3 is successful, the result will not be affected by the transformation.
     * <p/>
     * For example, when the request to download an image from the Internet fails, provide a default one.
     *
     * @param transform
     * @return
     */
    public Promise3<Success, Failure, Progress> recover(final Transformation<Failure, Success> transform);

    /**
     * Creates a new Promise3 and tries to transform a failure into a success. If the transformation fails, the result
     * will be also a failure.
     * <p/>
     * For example, when the request to download an image from the Internet fails, try to provide a local cached copy,
     * but if there is no image in the cache, the Promise3 will fail.
     *
     * @param transform
     * @return
     */
    public Promise3<Success, Failure, Progress> flatRecover(final Transformation<Failure, Either<Failure, Success>> transform);

    /**
     * Pipes the success of a Promise3 into another asynchronous operation and returns a new Promise3 for that operation.
     * <p/>
     * For example, make a request to a server, then when you have a successful result, make another request.
     * The result of the pipe is a new Promise3 for the second operation.
     *
     * @param transform
     * @param <Success2>
     * @return
     */
    public <Success2> Promise3<Success2, Failure, Void> pipe(final Pipe3<Success, Success2, Failure> transform);


    /**
     * Pipes the failure of a Promise3 into another asynchronous operation and returns a new Promise3 for that operation.
     * <p/>
     * For example, make a request to a server and if that fails, make another request to a different server.
     *
     * @param transform
     * @return
     */
    public Promise3<Success, Failure, Void> recoverWith(final Pipe3<Failure, Success, Failure> transform);

    /**
     * Creates a new promise which makes sure the success, failure and progress callbacks are run on the UI thread.
     *
     * @return
     */
    public Promise3<Success, Failure, Progress> runOnUiThread();

}
