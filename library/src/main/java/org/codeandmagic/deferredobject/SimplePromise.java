package org.codeandmagic.deferredobject;

/**
 * Simplified version of {@link org.codeandmagic.deferredobject.Promise} which returns a {@link java.lang.Throwable}
 * in case of failure and has no support for progress.
 *
 * Created by cristian on 10/02/2014.
 */
public interface SimplePromise<Success> extends Promise<Success, Throwable, Void> {

    public <Success2> SimplePromise<Success2> map(MapTransformation<Success, Success2> transform);

}
