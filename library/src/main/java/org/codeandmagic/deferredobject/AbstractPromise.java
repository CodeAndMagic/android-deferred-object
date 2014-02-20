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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: cvrabie1 Date: 10/07/2012
 */
public class AbstractPromise<Success, Failure, Progress> implements Promise<Success, Failure, Progress> {

    static <T> List<Callback<T>> arr() {
        return new CopyOnWriteArrayList<Callback<T>>();
    }

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

    protected final List<Callback<Success>> successCallbacks;
    protected final List<Callback<Failure>> failureCallbacks;
    protected final List<Callback<Progress>> progressCallbacks;
    protected final List<Callback<Either<Failure, Success>>> completeCallbacks;

    private AbstractPromise(List<Callback<Success>> successCallbacks,
                            List<Callback<Failure>> failureCallbacks,
                            List<Callback<Progress>> progressCallbacks,
                            List<Callback<Either<Failure, Success>>> completeCallbacks) {
        this.successCallbacks = successCallbacks;
        this.failureCallbacks = failureCallbacks;
        this.progressCallbacks = progressCallbacks;
        this.completeCallbacks = completeCallbacks;
    }

    public AbstractPromise() {
        this(AbstractPromise.<Success>arr(),
                AbstractPromise.<Failure>arr(),
                AbstractPromise.<Progress>arr(),
                AbstractPromise.<Either<Failure, Success>>arr());
    }

    protected <S, F, P> AbstractPromise<S, F, P> newPromise() {
        return new AbstractPromise<S, F, P>();
    }


    protected void success(final Success resolved) {
        if (state == State.PENDING) {
            this.result = resolved;
            this.state = State.SUCCESS;
            triggerSuccess();
        } else {
            throw new IllegalStateException("Can't complete a Promise which is in state '" + state.name() + "'.");
        }
    }

    protected final void triggerSuccess() {
        triggerCompleted();
        for (final Callback<Success> s : successCallbacks) {
            s.onCallback(result);
        }
    }

    protected void failure(final Failure failure) {
        if (state == State.PENDING) {
            this.failure = failure;
            this.state = State.FAILED;
            triggerFailure();
        } else {
            throw new IllegalStateException("Can't fail a Promise which is in state '" + state.name() + "'.");
        }
    }

    protected final void triggerFailure() {
        triggerCompleted();
        for (final Callback<Failure> f : failureCallbacks) {
            f.onCallback(failure);
        }
    }

    protected final void complete(Either<Failure, Success> either) {
        if (either.isLeft()) failure(either.getLeft());
        else success(either.getRight());
    }

    protected final void triggerCompleted() {
        for (final Callback<Either<Failure, Success>> c : completeCallbacks) {
            c.onCallback(isSuccess() ? new Right<Failure, Success>(result) : new Left<Failure, Success>(failure));
        }
    }

    protected void progress(final Progress progress) {
        if (State.PENDING.compareTo(state) < 0) return;
        for (final Callback<Progress> p : progressCallbacks) {
            p.onCallback(progress);
        }
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
    public Promise<Success, Failure, Progress> andThen(Callback<Success> onSuccess,
                                                       Callback<Failure> onFailure,
                                                       Callback<Progress> onProgress) {

        if (onSuccess != null) successCallbacks.add(onSuccess);
        if (onFailure != null) failureCallbacks.add(onFailure);
        if (onProgress != null) progressCallbacks.add(onProgress);

        if (onSuccess != null && isSuccess()) onSuccess.onCallback(result);
        if (onFailure != null && isFailure()) onFailure.onCallback(failure);

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
        if (onComplete != null && (isSuccess() || isFailure()))
            onComplete.onCallback(isSuccess() ? new Right<Failure, Success>(result) : new Left<Failure, Success>(failure));

        return this;
    }


    @SuppressWarnings("unchecked")
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
    map(final EitherMapTransformation<Success, Success2, Failure> transform) {

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

    @Override
    public Promise<Success, Failure, Progress> recover(final MapTransformation<Failure, Success> transform) {

        final AbstractPromise<Success, Failure, Progress> promise = newPromise();

        this.onSuccess(new Callback<Success>() {
            @Override
            public void onCallback(Success result) {
                promise.success(result);
            }
        }).onFailure(new Callback<Failure>() {
            @Override
            public void onCallback(Failure result) {
                promise.success(transform.transform(result));
            }
        }).onProgress(new Callback<Progress>() {
            @Override
            public void onCallback(Progress result) {
                promise.progress(result);
            }
        });

        return promise;
    }

    @Override
    public Promise<Success, Failure, Progress> recover(final EitherMapTransformation<Failure, Success, Failure> transform) {

        final AbstractPromise<Success, Failure, Progress> promise = newPromise();

        this.onSuccess(new Callback<Success>() {
            @Override
            public void onCallback(Success result) {
                promise.success(result);
            }
        }).onFailure(new Callback<Failure>() {
            @Override
            public void onCallback(Failure result) {
                promise.complete(transform.transform(result));
            }
        }).onProgress(new Callback<Progress>() {
            @Override
            public void onCallback(Progress result) {
                promise.progress(result);
            }
        });

        return promise;
    }

    @Override
    public <Success2> Promise<Success2, Failure, Void> pipe(final PipeTransformation<Success, Success2, Failure> transform) {

        final AbstractPromise<Success2, Failure, Void> promise = newPromise();

        this.onSuccess(new Callback<Success>() {
            @Override
            public void onCallback(Success result) {
                // When the first operation succeeds, initiate the second operation
                transform.transform(result).onComplete(new Callback<Either<Failure, Success2>>() {
                    @Override
                    public void onCallback(Either<Failure, Success2> result2) {
                        // When the second operation completes, complete the new Promise
                        promise.complete(result2);
                    }
                });
            }
        }).onFailure(new Callback<Failure>() {
            @Override
            public void onCallback(Failure result) {
                promise.failure(result);
            }
        });

        return promise;
    }

    @Override
    public Promise<Success, Failure, Void> recoverWith(final PipeTransformation<Failure, Success, Failure> transform) {

        final AbstractPromise<Success, Failure, Void> promise = newPromise();

        this.onSuccess(new Callback<Success>() {
            @Override
            public void onCallback(Success result) {
                promise.success(result);
            }
        }).onFailure(new Callback<Failure>() {
            @Override
            public void onCallback(Failure result) {
                // When the first operation fails, initiate a second operation
                transform.transform(result).onComplete(new Callback<Either<Failure, Success>>() {
                    @Override
                    public void onCallback(Either<Failure, Success> result2) {
                        // When the second operation completes, complete the new Promise
                        promise.complete(result2);
                    }
                });
            }
        });

        return promise;
    }
}
