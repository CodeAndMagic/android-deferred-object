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
 * Created by cristian on 10/02/2014.
 */
public final class Right<T1, T2> extends Either<T1, T2> {

    private final T2 right;

    public Right(T2 right) {
        this.right = right;
    }

    @Override
    public boolean isRight() {
        return true;
    }

    @Override
    public T2 getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Right right1 = (Right) o;

        if (right != null ? !right.equals(right1.right) : right1.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return right != null ? right.hashCode() : 0;
    }
}
