package org.codeandmagic.deferredobject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.codeandmagic.deferredobject.android.DeferredHttpUrlConnection;

import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockFragmentActivity implements
		ResolveCallback<HttpURLConnection>, RejectCallback<HttpURLConnection> {

	private static final String TAG = "Test";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		setup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	private void setup() {
		try {
			Promise<HttpURLConnection, HttpURLConnection, Void> p1 = new DeferredHttpUrlConnection(
					new URL("http://www.google.com"));

			p1.done(this);
			p1.fail(this);
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onResolve(HttpURLConnection resolved) {
		Log.d(TAG, "Done: " + resolved);
	}

	@Override
	public void onReject(HttpURLConnection rejected) {
		Log.w(TAG, "Fail: " + rejected);

	}
}
