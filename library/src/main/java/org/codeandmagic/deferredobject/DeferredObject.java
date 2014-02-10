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

/**
 * User: cvrabie1 Date: 09/07/2012
 */
public abstract class DeferredObject<Success, Failure, Progress> extends AbstractPromise<Success, Failure, Progress> {

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
