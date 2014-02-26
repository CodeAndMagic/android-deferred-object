package org.codeandmagic.promise.http;

import org.apache.http.HttpResponse;
import org.codeandmagic.promise.Either;
import org.codeandmagic.promise.Either.Left;
import org.codeandmagic.promise.Either.Right;
import org.codeandmagic.promise.Transformation;

import java.util.Arrays;

/**
 * Created by evelina on 26/02/2014.
 */
public abstract class StatusCodeSelector implements Transformation<HttpResponse, Either<Throwable, HttpResponse>> {
    public abstract boolean isAcceptable(int statusCode);

    @Override
    public Either<Throwable, HttpResponse> transform(HttpResponse value) {
        final int statusCode = value.getStatusLine().getStatusCode();
        return isAcceptable(statusCode) ? new Right<Throwable, HttpResponse>(value) :
                new Left<Throwable, HttpResponse>(new IllegalStateException("Expecting status code '" + toString()
                        + "' but got " + statusCode));
    }

    public static class RangeStatusCodeSelector extends StatusCodeSelector {
        public final int minStatusCode;
        public final int maxStatusCode;

        public RangeStatusCodeSelector(int minStatusCode, int maxStatusCode) {
            this.minStatusCode = minStatusCode;
            this.maxStatusCode = maxStatusCode;
        }

        @Override
        public boolean isAcceptable(int statusCode) {
            return statusCode >= minStatusCode && statusCode <= maxStatusCode;
        }

        @Override
        public String toString() {
            return minStatusCode + " - " + maxStatusCode;
        }
    }

    public static class ArrayStatusCodeSelector extends StatusCodeSelector {
        public final int[] statuses;

        public ArrayStatusCodeSelector(int[] statuses) {
            this.statuses = statuses;
        }

        @Override
        public boolean isAcceptable(int statusCode) {
            if (statuses == null) return false;
            for (int status : statuses) {
                if (statusCode == status) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return Arrays.toString(statuses);
        }
    }

    public static class SingleStatusCodeSelector extends StatusCodeSelector {
        public final int statusCode;

        public SingleStatusCodeSelector(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public boolean isAcceptable(int statusCode) {
            return this.statusCode == statusCode;
        }

        @Override
        public String toString() {
            return String.valueOf(statusCode);
        }
    }
}
