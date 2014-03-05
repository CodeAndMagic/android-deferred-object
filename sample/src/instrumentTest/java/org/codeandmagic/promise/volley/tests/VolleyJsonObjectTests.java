package org.codeandmagic.promise.volley.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.Transformation;
import org.codeandmagic.promise.sample.MergePromiseActivity;
import org.codeandmagic.promise.volley.VolleyJsonPromise;
import org.codeandmagic.promise.volley.VolleyRequest.RObject;
import org.json.JSONObject;

import static org.mockito.Mockito.*;

/**
 * Created by evelina on 02/03/2014.
 */
public class VolleyJsonObjectTests extends ActivityInstrumentationTestCase2<MergePromiseActivity> {

    public static final String SUCCESS_URL = "http://date.jsontest.com/";
    public static final String FAILURE_URL = "http://example.com/";

    public VolleyJsonObjectTests() {
        super(MergePromiseActivity.class);
    }

    public void testSuccessRequest() {
        Callback<JSONObject> successCallback = mock(Callback.class);
        Callback<Throwable> failureCallback = mock(Callback.class);

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        VolleyJsonPromise.jsonObjectPromise(queue, Method.GET, SUCCESS_URL, null)
                .onSuccess(successCallback)
                .onFailure(failureCallback)
                .pipe(VolleyJsonPromise.<JSONObject>jsonObjectPipe(queue, SUCCESS_URL, null))
                .onSuccess(successCallback)
                .onFailure(failureCallback);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(successCallback, times(2)).onCallback(any(JSONObject.class));
        verify(failureCallback, never()).onCallback(any(Throwable.class));
    }

    public void testFailedRequest() {
        Callback<JSONObject> successCallback = mock(Callback.class);
        Callback<Throwable> failureCallback = mock(Callback.class);

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        VolleyJsonPromise.jsonObjectPromise(queue, Method.GET, FAILURE_URL, null)
                .onSuccess(successCallback)
                .onFailure(failureCallback);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(successCallback, never()).onCallback(any(JSONObject.class));
        verify(failureCallback, only()).onCallback(any(Throwable.class));
    }

    public void testExtendedRequest() {
        Callback<JSONObject> successCallback = mock(Callback.class);
        Callback<Throwable> failureCallback = mock(Callback.class);

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        VolleyJsonPromise.jsonObjectPromise(Method.GET, SUCCESS_URL, null)
                .map(new Transformation<RObject, RObject>() {
                    @Override
                    public RObject transform(RObject value) {
                        value.getRequest().setTag("TAG");
                        return value;
                    }
                })
                .pipe(VolleyJsonPromise.queueJsonObjectRequest(queue))
                .onSuccess(successCallback)
                .onFailure(failureCallback);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(successCallback, only()).onCallback(any(JSONObject.class));
        verify(failureCallback, never()).onCallback(any(Throwable.class));
    }
}
