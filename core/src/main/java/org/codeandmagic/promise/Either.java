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
 * Created by cristian on 10/02/2014.
 */
public abstract class Either<T1, T2> {

    public static final class Left<T1, T2> extends Either<T1, T2> {

        private final T1 left;

        public Left(T1 left) {
            this.left = left;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public T1 getLeft() {
            return left;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Left left1 = (Left) o;

            if (left != null ? !left.equals(left1.left) : left1.left != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return left != null ? left.hashCode() : 0;
        }
    }

    public static final class Right<T1, T2> extends Either<T1, T2> {

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


    public boolean isLeft() {
      return false;
    }

    public T1 getLeft() {
       return null;
    }

    public boolean isRight() {
      return false;
    }

    public T2 getRight() {
       return null;
    }
}
