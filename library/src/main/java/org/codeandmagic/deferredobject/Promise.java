/*
 * Copyright (c) 2014 Cristian Vrabie, Evelina Vrabie.
 *
 * This file is part of android-deferred-object.
 * android-deferred-object is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License,or (at your option) any later version.
 *
 * android-deferred-object is distributed in the hope that it will be useful, butWITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with android-deferred-object.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.codeandmagic.deferredobject;

/**
 * User: cvrabie1 Date: 09/07/2012
 */
public interface Promise<Success, Failure, Progress> {

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
    public Promise<Success, Failure, Progress> onComplete(final Callback<Either<Failure, Success>> onComplete);

    /**
     * Called when the operation completes with success.
     *
     * @param onSuccess
     * @return
     */
    public Promise<Success, Failure, Progress> onSuccess(final Callback<Success> onSuccess);

    /**
     * Called when the operation completes with a failure.
     *
     * @param onFailure
     * @return
     */
    public Promise<Success, Failure, Progress> onFailure(final Callback<Failure> onFailure);

    /**
     * Called when there is a progress update on the operation.
     *
     * @param onProgress
     * @return
     */
    public Promise<Success, Failure, Progress> onProgress(final Callback<Progress> onProgress);


    /**
     * Registers callbacks for success, failure and progress. The Promise is left unchanged and can be
     * chained.
     *
     * @param onSuccess
     * @param onFailure
     * @param onProgress
     * @return
     */
    public Promise<Success, Failure, Progress> andThen(Callback<Success> onSuccess, Callback<Failure> onFailure,
                                                       Callback<Progress> onProgress);

    /* -------------------------------------------------------------------------------------- */
    // Transformations
    /* -------------------------------------------------------------------------------------- */

    /**
     * Creates a new Promise by applying a {@link MapTransformation} to
     * the success result of this Promise.
     *
     * @param transform  transforms Success into Success2
     * @param <Success2>
     * @return
     */
    public <Success2> Promise<Success2, Failure, Progress>
    map(final MapTransformation<Success, Success2> transform);

    /**
     * Creates a new Promise by applying {@link MapTransformation}s to the success and
     * failure results of this Promise.
     *
     * @param transformSuccess transformation between success types or null if you want it unchanged
     * @param transformFailure transformation between failure types or null if you want it unchanged
     * @param <Success2>
     * @param <Failure2>
     * @return
     */
    public <Success2, Failure2> Promise<Success2, Failure2, Progress>
    map(final MapTransformation<Success, Success2> transformSuccess,
        final MapTransformation<Failure, Failure2> transformFailure);

    /**
     * Creates a new Promise by applying {@link MapTransformation}s to the success, failure
     * and progress results of this Promise.
     *
     * @param transformSuccess  transformation between success types or null if you want it unchanged
     * @param transformFailure  transformation between failure types or null if you want it unchanged
     * @param transformProgress transformation between progress types or null if you want it unchanged
     * @param <Success2>
     * @param <Failure2>
     * @param <Progress2>
     * @return
     */
    public <Success2, Failure2, Progress2> Promise<Success2, Failure2, Progress2>
    map(final MapTransformation<Success, Success2> transformSuccess,
        final MapTransformation<Failure, Failure2> transformFailure,
        final MapTransformation<Progress, Progress2> transformProgress);

    /**
     * Creates a new Promise by applying a {@link org.codeandmagic.deferredobject.FlatMapTransformation}.
     * By contrast to a {@link org.codeandmagic.deferredobject.MapTransformation}, this can transform a successful
     * Promise into a failed one.
     * <p/>
     * Example: a successful HTTP call can be transformed into a failed one if the JSON de-serialisation (expressed
     * as a {@link org.codeandmagic.deferredobject.FlatMapTransformation}) throws an error.
     *
     * @param transform
     * @param <Success2>
     * @return
     */
    public <Success2> Promise<Success2, Failure, Progress>
    flatMap(final FlatMapTransformation<Success, Success2, Failure> transform);
}
