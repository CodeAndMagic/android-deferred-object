package org.codeandmagic.deferredobject;

/**
 * Created by cristian on 10/02/2014.
 */
public interface Callback<T> {

    void onCallback(final T result);
}
