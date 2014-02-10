package org.codeandmagic.deferredobject;

/**
 * Created by cristian on 10/02/2014.
 */
public abstract class Either<T1, T2> {

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
