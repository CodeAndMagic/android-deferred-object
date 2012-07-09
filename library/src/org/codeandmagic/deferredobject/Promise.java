package org.codeandmagic.deferredobject;

/** User: cvrabie1 Date: 09/07/2012 */
public interface Promise<Resolved, Rejected, Progress> {

  public enum State {
    PENDING, REJECTED, RESOLVED
  }

  public static interface CompleteCallback<Resolved, Rejected> {
    public void onComplete(final Resolved resolved, final Rejected rejected);
  }

  public static interface ResolveCallback<Resolved> {
    public void onResolve(final Resolved resolved);
  }

  public static interface RejectCallback<Rejected> {
    public void onReject(final Rejected rejected);
  }

  public static interface ProgressCallback<Progress> {
    public void onProgress(final Progress progress);
  }

  public State getState();
  public boolean isPending();
  public boolean isRejected();
  public boolean isResolved();

  public Promise<Resolved, Rejected, Progress> then(final ResolveCallback<Resolved> onResolve);

  public Promise<Resolved, Rejected, Progress> then(final ResolveCallback<Resolved> onResolve, final RejectCallback<Rejected> onReject);

  public Promise<Resolved, Rejected, Progress> then(final ResolveCallback<Resolved> onResolve, final RejectCallback<Rejected> onReject, final ProgressCallback<Progress> onProgress);

  public Promise<Resolved, Rejected, Progress> then(final ResolveCallback<Resolved> onResolve, final RejectCallback<Rejected> onReject, final ProgressCallback<Progress> onProgress, final CompleteCallback<Resolved,Rejected> onComplete);

  public Promise<Resolved, Rejected, Progress> always(final CompleteCallback<Resolved,Rejected> onComplete);

  public Promise<Resolved, Rejected, Progress> done(final ResolveCallback<Resolved> onResolve);

  public Promise<Resolved, Rejected, Progress> fail(final RejectCallback<Rejected> onReject);

  public Promise<Resolved, Rejected, Progress> progress(final ProgressCallback<Progress> onProgress);

  /**
   * Returns a new promise that filters the status and values of a deferred through a function.
   * @param resolveFilter
   * @param rejectFilter
   * @param progressFilter
   * @param <Resolved2>
   * @param <Rejected2>
   * @param <Progress2>
   * @return
   */
  public <Resolved2, Rejected2, Progress2> Promise<Resolved2, Rejected2, Progress2>
         pipe(final PipedPromise.ResolveFilter<Resolved, Resolved2> resolveFilter,
              final PipedPromise.RejectFilter<Rejected, Rejected2> rejectFilter,
              final PipedPromise.ProgressFilter<Progress, Progress2> progressFilter);

  public <Resolved2, Rejected2, Progress2> Promise<Resolved2, Rejected2, Progress2>
  pipe(final PipedPromise.ResolveFilter<Resolved, Resolved2> resolveFilter);

}

