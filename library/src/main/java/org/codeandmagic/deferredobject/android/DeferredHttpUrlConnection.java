package org.codeandmagic.deferredobject.android;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeferredHttpUrlConnection extends
		DeferredAsyncTask<HttpURLConnection, HttpURLConnection, Float> {

	private final URL url;

	public DeferredHttpUrlConnection(URL url) {
		this.url = url;
	}

	protected HttpURLConnection onPreExecute(HttpURLConnection connection) {
		return connection;
	}

	@Override
	protected HttpURLConnection convertResultToFailure(HttpURLConnection resolved) {
		try {
			if (resolved.getResponseCode() >= 300) return resolved;
			else return null;
		}
		catch (IOException e) {
			return resolved;
		}
	}

	@Override
	protected HttpURLConnection doInBackground() throws Exception {
		final HttpURLConnection connection = onPreExecute((HttpURLConnection) url.openConnection());
		connection.getInputStream();
		return connection;
	}

}
