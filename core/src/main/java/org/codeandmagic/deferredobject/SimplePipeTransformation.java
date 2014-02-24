package org.codeandmagic.deferredobject;

/**
 *  Transforms T1 into a Promise which provides T2 in case of success and Throwable in case of failure.
 *
 * For example, it can be used to make a request to the Internet then use the result of the first one into a second request.
 * Created by cristian on 11/02/2014.
 */
public interface SimplePipeTransformation<T1, T2>  {

    SimplePromise<T2> transform(T1 value);
}
