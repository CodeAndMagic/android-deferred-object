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

import android.os.Handler;
import android.os.Looper;
import org.codeandmagic.promise.*;
import org.codeandmagic.promise.Either.Left;
import org.codeandmagic.promise.Either.Right;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: cvrabie1 Date: 10/07/2012
 */
public class AbstractPromise3<Success, Failure, Progress> implements Promise3<Success, Failure, Progress> {

    static <T> List<Callback<T>> arr() {
        return new CopyOnWriteArrayList<Callback<T>>();
    }

    /*
    * The state of this Deferred Object
    */
    protected Promise3.State state = State.PENDING;
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

    private AbstractPromise3(List<Callback<Success>> successCallbacks,
                             List<Callback<Failure>> failureCallbacks,
                             List<Callback<Progress>> progressCallbacks,
                             List<Callback<Either<Failure, Success>>> completeCallbacks) {
        this.successCallbacks = successCallbacks;
        this.failureCallbacks = failureCallbacks;
        this.progressCallbacks = progressCallbacks;
        this.completeCallbacks = completeCallbacks;
    }

    public AbstractPromise3() {
        this(AbstractPromise3.<Success>arr(),
                AbstractPromise3.<Failure>arr(),
                AbstractPromise3.<Progress>arr(),
                AbstractPromise3.<Either<Failure, Success>>arr());
    }

    protected <S, F, P> AbstractPromise3<S, F, P> newPromise() {
        return new AbstractPromise3<S, F, P>();
    }


    protected void success(final Success resolved) {
        if (state == State.PENDING) {
            this.result = resolved;
            this.state = State.SUCCESS;
            triggerSuccess();
        } else {
            throw new IllegalStateException("Can't complete a Promise3 which is in state '" + state.name() + "'.");
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
            throw new IllegalStateException("Can't fail a Promise3 which is in state '" + state.name() + "'.");
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
    public Promise3.State state() {
        return state;
    }

    @Override
    public boolean isPending() {
        return Promise3.State.PENDING == state;
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
    public Promise3<Success, Failure, Progress> andThen(Callback<Success> onSuccess,
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
    public Promise3<Success, Failure, Progress> onSuccess(Callback<Success> onSuccess) {
        return andThen(onSuccess, null, null);
    }

    @Override
    public Promise3<Success, Failure, Progress> onFailure(Callback<Failure> onFailure) {
        return andThen(null, onFailure, null);
    }

    @Override
    public Promise3<Success, Failure, Progress> onProgress(Callback<Progress> onProgress) {
        return andThen(null, null, onProgress);
    }

    @Override
    public Promise3<Success, Failure, Progress> onComplete(Callback<Either<Failure, Success>> onComplete) {
        if (onComplete != null) completeCallbacks.add(onComplete);
        if (onComplete != null && (isSuccess() || isFailure()))
            onComplete.onCallback(isSuccess() ? new Right<Failure, Success>(result) : new Left<Failure, Success>(failure));

        return this;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <Success2, Failure2, Progress2> Promise3<Success2, Failure2, Progress2>
    map(final Transformation<Success, Success2> transformSuccess,
        final Transformation<Failure, Failure2> transformFailure,
        final Transformation<Progress, Progress2> transformProgress) {

        final AbstractPromise3<Success2, Failure2, Progress2> promise = newPromise();

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
    public <Success2> Promise3<Success2, Failure, Progress>
    map(Transformation<Success, Success2> transform) {
        return map(transform, null, null);
    }

    @Override
    public <Success2, Failure2> Promise3<Success2, Failure2, Progress>
    map(Transformation<Success, Success2> transformSuccess,
        Transformation<Failure, Failure2> transformFailure) {
        return map(transformSuccess, transformFailure, null);
    }

    @Override
    public <Success2> Promise3<Success2, Failure, Progress>
    flatMap(final Transformation<Success, Either<Failure, Success2>> transform) {

        final AbstractPromise3<Success2, Failure, Progress> promise = newPromise();

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
    public Promise3<Success, Failure, Progress> recover(final Transformation<Failure, Success> transform) {

        final AbstractPromise3<Success, Failure, Progress> promise = newPromise();

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
    public Promise3<Success, Failure, Progress> flatRecover(final Transformation<Failure, Either<Failure, Success>> transform) {

        final AbstractPromise3<Success, Failure, Progress> promise = newPromise();

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
    public <Success2> Promise3<Success2, Failure, Void> pipe(final Pipe3<Success, Success2, Failure> transform) {

        final AbstractPromise3<Success2, Failure, Void> promise = newPromise();

        this.onSuccess(new Callback<Success>() {
            @Override
            public void onCallback(Success result) {
                // When the first operation succeeds, initiate the second operation
                transform.transform(result).onComplete(new Callback<Either<Failure, Success2>>() {
                    @Override
                    public void onCallback(Either<Failure, Success2> result2) {
                        // When the second operation completes, complete the new Promise3
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
    public Promise3<Success, Failure, Void> recoverWith(final Pipe3<Failure, Success, Failure> transform) {

        final AbstractPromise3<Success, Failure, Void> promise = newPromise();

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
                        // When the second operation completes, complete the new Promise3
                        promise.complete(result2);
                    }
                });
            }
        });

        return promise;
    }

    @Override
    public Promise3<Success, Failure, Progress> runOnUiThread() {
        final AbstractPromise3<Success, Failure, Progress> promise = newPromise();
        final Handler mainHandler = new Handler(Looper.getMainLooper());

        this.onComplete(new Callback<Either<Failure, Success>>() {
            @Override
            public void onCallback(final Either<Failure, Success> result) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        promise.complete(result);
                    }
                });
            }
        }).onProgress(new Callback<Progress>() {
            @Override
            public void onCallback(final Progress result) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        promise.progress(result);
                    }
                });
            }
        });

        return promise;
    }
}
