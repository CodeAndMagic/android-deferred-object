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
 * Created by cristian on 10/02/2014.
 */
public class AbstractSimplePromise<Success> extends AbstractPromise<Success, Throwable, Void> implements SimplePromise<Success> {

    @Override
    protected <S, F, P> AbstractPromise<S, F, P> newPromise() {
        return (AbstractPromise<S, F, P>) new AbstractSimplePromise<S>();
    }

    @Override
    public <Success2> SimplePromise<Success2> map(final MapTransformation<Success, Success2> transform) {
       return (SimplePromise<Success2>) super.map(transform);
    }
}
