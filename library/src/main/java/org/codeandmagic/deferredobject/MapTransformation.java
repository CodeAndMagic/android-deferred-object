package org.codeandmagic.deferredobject;

/**
 * Created by cristian on 10/02/2014.
 */
public interface MapTransformation<T1, T2> {

    T2 transform(T1 value);

}