package org.codeandmagic.deferredobject.tests;

import junit.framework.TestCase;
import org.codeandmagic.deferredobject.Callback;
import org.codeandmagic.deferredobject.Either;
import org.codeandmagic.deferredobject.Right;

import static org.mockito.Mockito.*;

/**
 * Created by cristian on 10/02/2014.
 */
public class CallbackTests extends TestCase {

    public void testCallbacks() {
        InspectableDeferredObject<Integer, Void, Void> promise = new InspectableDeferredObject<Integer, Void, Void>();
        Callback<Integer> onSuccess = mock(Callback.class);
        Callback<Either<Void, Integer>> onComplete = mock(Callback.class);

        promise.onSuccess(onSuccess).onComplete(onComplete);

        promise.success(3);

        verify(onSuccess, only()).onCallback(3);
        verify(onComplete, only()).onCallback(new Right<Void, Integer>(3));
    }
}
