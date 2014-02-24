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

/**
 * A DeferredObject3 is a {@link org.codeandmagic.promise.Promise3} that exposes methods which allow setting
 * its completion status.
 * <p/>
 * User: cvrabie1 Date: 09/07/2012
 */
public class DeferredObject3<Success, Failure, Progress> extends AbstractPromise3<Success, Failure, Progress> {

    public static <S, F, P> DeferredObject3<S, F, P> successful(S value) {
        final DeferredObject3<S, F, P> deferredObject = new DeferredObject3<S, F, P>();
        deferredObject.success(value);
        return deferredObject;
    }

    public static <S, F, P> DeferredObject3<S, F, P> failed(F value) {
        final DeferredObject3<S, F, P> deferredObject = new DeferredObject3<S, F, P>();
        deferredObject.failure(value);
        return deferredObject;
    }

    @Override
    public final void progress(Progress progress) {
        super.progress(progress);
    }

    @Override
    public final void success(Success resolved) {
        super.success(resolved);
    }

    @Override
    public final void failure(Failure failure) {
        super.failure(failure);
    }
}
