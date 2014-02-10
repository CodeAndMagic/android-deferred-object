package org.codeandmagic.deferredobject;

/**
 * Created by cristian on 10/02/2014.
 */
public interface FlatMapTransformation<T1, T2, F> {

    Either<F, T2> transform(T1 value);
}
