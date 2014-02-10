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
