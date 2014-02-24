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

package org.codeandmagic.promise;

/**
 * Transforms T1 into T2 in situations when it is guaranteed this operation cannot fail.
 * For example, transforming an int into a String.
 *
 * For operations that can fail, for example transforming a String into an int (parse), must use a
 * {@link EitherMapTransformation}.
 *
 * Created by cristian on 10/02/2014.
 */
public interface MapTransformation<T1, T2> {

    T2 transform(T1 value);

}