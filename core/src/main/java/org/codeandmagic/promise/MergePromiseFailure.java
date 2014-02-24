package org.codeandmagic.promise;

/**
 * Created by cristian on 15/02/2014.
 */
public class MergePromiseFailure extends Exception {

    public final Throwable[] failures;

    public MergePromiseFailure(String message, Throwable[] failures) {
        super(message);
        this.failures = failures;
    }
}
