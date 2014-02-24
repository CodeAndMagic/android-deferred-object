package org.codeandmagic.promise;

/**
 *  Transforms T1 into a Promise3 which provides T2 in case of success and Throwable in case of failure.
 *
 * For example, it can be used to make a request to the Internet then use the result of the first one into a second request.
 * Created by cristian on 11/02/2014.
 */
public interface Pipe<T1, T2>  {

    Promise<T2> transform(T1 value);
}
