package org.codeandmagic.deferredobject;

/**
 * Created by cristian on 10/02/2014.
 */
public final class Left<T1, T2> extends Either<T1, T2> {

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
