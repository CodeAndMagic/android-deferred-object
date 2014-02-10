package org.codeandmagic.deferredobject.tests;

import org.codeandmagic.deferredobject.DeferredObject;

/**
 * Created by cristian on 10/02/2014.
 */
public class InspectableDeferredObject<Success, Failure, Progress> extends DeferredObject<Success, Failure, Progress> {


    public Success getSuccess() {
        return result;
    }

    public Failure getFailure() {
        return failure;
    }
}
