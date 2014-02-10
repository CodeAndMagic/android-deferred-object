/*
 * Copyright 2012 Cristian Vrabie, Evelina Vrabie
 *
 * This file is part of android-deferred-object.
 *
 * android-deferred-object is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * android-deferred-object is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.codeandmagic.deferredobject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: cvrabie1 Date: 10/07/2012
 */
public class AbstractPromise<Success, Failure, Progress> implements Promise<Success, Failure, Progress> {

    /*
    * The state of this Deferred Object
    */
    protected Promise.State state = State.PENDING;
    /**
     * The value or this deferred object if it has been result or null otherwise
     */
    protected Success result;
    /**
     * The rejection reason of this deferred object if it has been failure or null otherwise
     */
    protected Failure failure;

    protected final List<Callback<Success>> successCallbacks = new CopyOnWriteArrayList<Callback<Success>>();
    protected final List<Callback<Failure>> failureCallbacks = new CopyOnWriteArrayList<Callback<Failure>>();
    protected final List<Callback<Progress>> progressCallbacks = new CopyOnWriteArrayList<Callback<Progress>>();
    protected final List<Callback<Either<Failure, Success>>> completeCallbacks =
            new CopyOnWriteArrayList<Callback<Either<Failure, Success>>>();

    protected final void triggerSuccess() {
        triggerCompleted();
        for (final Callback<Success> r : successCallbacks) {
            r.onCallback(result);
        }
    }

    protected void success(final Success resolved) {
        this.result = resolved;
        this.state = State.SUCCESS;
        triggerSuccess();
    }

    protected final void triggerFailure() {
        triggerCompleted();
        for (final Callback<Failure> r : failureCallbacks) {
            r.onCallback(failure);
        }
    }

    protected void failure(final Failure failure) {
        this.failure = failure;
        this.state = State.FAILED;
        triggerFailure();
    }

    protected final void triggerCompleted() {
        for (final Callback<Either<Failure, Success>> c : completeCallbacks) {
            c.onCallback(isSuccess() ? new Right<Failure, Success>(result) : new Left<Failure, Success>(failure));
        }
    }

    protected void progress(final Progress progress) {
        if (Promise.State.PENDING.compareTo(state) < 0) return;
        for (final Callback<Progress> p : progressCallbacks) {
            p.onCallback(progress);
        }
    }

    protected <S, F, P> AbstractPromise<S, F, P> newPromise() {
        return new AbstractPromise<S, F, P>();
    }

    protected final void complete(Either<Failure, Success> either) {
        if (either.isLeft()) failure(either.getLeft());
        else success(either.getRight());
    }

    @Override
    public Promise.State state() {
        return state;
    }

    @Override
    public boolean isPending() {
        return Promise.State.PENDING == state;
    }

    @Override
    public boolean isFailure() {
        return State.FAILED == state;
    }

    @Override
    public boolean isSuccess() {
        return State.SUCCESS == state;
    }

    @Override
    public Promise<Success, Failure, Progress> andThen(Callback<Success> onSuccess, Callback<Failure> onFailure,
                                                       Callback<Progress> onProgress) {

        if (onSuccess != null) successCallbacks.add(onSuccess);
        if (onFailure != null) failureCallbacks.add(onFailure);
        if (onProgress != null) progressCallbacks.add(onProgress);

        if (onSuccess != null) if (isSuccess()) onSuccess.onCallback(result);
        if (onFailure != null) if (isFailure()) onFailure.onCallback(failure);

        return this;
    }

    @Override
    public Promise<Success, Failure, Progress> onSuccess(Callback<Success> onSuccess) {
        return andThen(onSuccess, null, null);
    }

    @Override
    public Promise<Success, Failure, Progress> onFailure(Callback<Failure> onFailure) {
        return andThen(null, onFailure, null);
    }

    @Override
    public Promise<Success, Failure, Progress> onProgress(Callback<Progress> onProgress) {
        return andThen(null, null, onProgress);
    }

    @Override
    public Promise<Success, Failure, Progress> onComplete(Callback<Either<Failure, Success>> onComplete) {
        if (onComplete != null) completeCallbacks.add(onComplete);
        if (onComplete != null) if (isSuccess() || isFailure())
            onComplete.onCallback(isSuccess() ? new Right<Failure, Success>(result) : new Left<Failure, Success>(failure));

        return this;
    }


    @Override
    public <Success2, Failure2, Progress2> Promise<Success2, Failure2, Progress2>
    map(final MapTransformation<Success, Success2> transformSuccess,
        final MapTransformation<Failure, Failure2> transformFailure,
        final MapTransformation<Progress, Progress2> transformProgress) {

        final AbstractPromise<Success2, Failure2, Progress2> promise = newPromise();

        this.onSuccess(new Callback<Success>() {
            @Override
            public void onCallback(Success result) {
                if (transformSuccess != null) {
                    promise.success(transformSuccess.transform(result));
                } else {
                    promise.success((Success2) result);
                }
            }
        }).onFailure(new Callback<Failure>() {
            @Override
            public void onCallback(Failure result) {
                if (transformFailure != null) {
                    promise.failure(transformFailure.transform(result));
                } else {
                    promise.failure((Failure2) result);
                }
            }
        }).onProgress(new Callback<Progress>() {
            @Override
            public void onCallback(Progress result) {
                if (transformProgress != null) {
                    promise.progress(transformProgress.transform(result));
                } else {
                    promise.progress((Progress2) result);
                }
            }
        });

        return promise;
    }

    @Override
    public <Success2> Promise<Success2, Failure, Progress>
    map(MapTransformation<Success, Success2> transform) {
        return map(transform, null, null);
    }

    @Override
    public <Success2, Failure2> Promise<Success2, Failure2, Progress>
    map(MapTransformation<Success, Success2> transformSuccess,
        MapTransformation<Failure, Failure2> transformFailure) {
        return map(transformSuccess, transformFailure, null);
    }

    @Override
    public <Success2> Promise<Success2, Failure, Progress>
    flatMap(final FlatMapTransformation<Success, Success2, Failure> transform) {

        final AbstractPromise<Success2, Failure, Progress> promise = newPromise();

        this.onSuccess(new Callback<Success>() {
            @Override
            public void onCallback(Success result) {
                promise.complete(transform.transform(result));
            }
        }).onFailure(new Callback<Failure>() {
            @Override
            public void onCallback(Failure result) {
                promise.failure(result);
            }
        }).onProgress(new Callback<Progress>() {
            @Override
            public void onCallback(Progress result) {
                promise.progress(result);
            }
        });

        return promise;
    }
}
