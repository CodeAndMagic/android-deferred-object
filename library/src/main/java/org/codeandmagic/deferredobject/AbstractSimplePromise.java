package org.codeandmagic.deferredobject;

/**
 * Created by cristian on 10/02/2014.
 */
public class AbstractSimplePromise<Success> extends AbstractPromise<Success, Throwable, Void> implements SimplePromise<Success> {

    @Override
    protected <S, F, P> AbstractPromise<S, F, P> newPromise() {
        return (AbstractPromise<S, F, P>) new AbstractSimplePromise<S>();
    }

    @Override
    public <Success2> SimplePromise<Success2> map(final MapTransformation<Success, Success2> transform) {
       return (SimplePromise<Success2>) super.map(transform);
    }
}
