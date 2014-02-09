package org.codeandmagic.deferredobject.android;

import org.codeandmagic.deferredobject.AbstractPromise;

import android.os.AsyncTask;

public abstract class DeferredAsyncTask<Resolved, Rejected, Progress> extends
		AbstractPromise<Resolved, Rejected, Progress> {

	public final AsyncTask<Void, Progress, Resolved> task;
	private Exception exception;

	public DeferredAsyncTask() {
		task = new AsyncTask<Void, Progress, Resolved>() {

			@Override
			protected void onCancelled() {
				DeferredAsyncTask.this.fail(null);
			}

			@Override
			protected void onPostExecute(Resolved resolved) {
				if (null == resolved) DeferredAsyncTask.this
						.reject(convertExceptionToFailure(exception));
				else {
					final Rejected rejected = convertResultToFailure(resolved);
					if (null == rejected) DeferredAsyncTask.this.resolve(resolved);
					else DeferredAsyncTask.this.reject(rejected);
				}
			}

			@Override
			protected void onProgressUpdate(Progress... progress) {
				DeferredAsyncTask.this.notify(progress[0]);
			}

			@Override
			protected Resolved doInBackground(Void... params) {
				try {
					return DeferredAsyncTask.this.doInBackground();
				}
				catch (Exception e) {
					exception = e;
					return null;
				}
			}
		}.execute();
	}

	protected abstract Resolved doInBackground() throws Exception;

	protected Rejected convertResultToFailure(Resolved resolved) {
		return null;
	}

	protected Rejected convertExceptionToFailure(Exception exception) {
		return null;
	}

	public static class TaskException extends RuntimeException {
		private static final long serialVersionUID = -4400838550293270727L;

		public TaskException(Throwable cause) {
			super(cause);
		}
	}
}
