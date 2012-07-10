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

  public static interface ResolveFilter<Resolved1,Resolved2>{
    public Resolved2 filterResolved(Resolved1 resolved);
  }

  public static interface RejectFilter<Rejected1, Rejected2>{
    public Rejected2 filterRejected(Rejected1 rejected);
  }

  public static interface ProgressFilter<Progress1, Progress2>{
    public Progress2 filterProgress(Progress1 progress);
  }

  public static class PassThroughResolveFilter<Resolved> implements ResolveFilter<Resolved,Resolved>{
    @Override
    public Resolved filterResolved(Resolved resolved) {
      return resolved;
    }
  }

  public static class PassThroughRejectFilter<Reject> implements RejectFilter<Reject,Reject>{
    @Override
    public Reject filterRejected(Reject rejected) {
      return rejected;
    }
  }

  public static class PassThroughProgressFilter<Progress> implements ProgressFilter<Progress,Progress>{
    @Override
    public Progress filterProgress(Progress progress) {
      return progress;
    }
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

  public  <Resolved2> Promise<Resolved2, Rejected, Progress>
          pipe(final PipedPromise.ResolveFilter<Resolved, Resolved2> resolveFilter);

}

