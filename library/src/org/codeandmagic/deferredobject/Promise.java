package org.codeandmagic.deferredobject;

import org.codeandmagic.deferredobject.pipe.ProgressFilter;
import org.codeandmagic.deferredobject.pipe.RejectFilter;
import org.codeandmagic.deferredobject.pipe.ResolveFilter;

/** User: cvrabie1 Date: 09/07/2012 */
public interface Promise<Resolved, Rejected, Progress> {

  public enum State {
    PENDING, REJECTED, RESOLVED
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



  public <Resolved2, Rejected2, Progress2> Promise<Resolved2, Rejected2, Progress2>
         pipe(final ResolveFilter<Resolved, Resolved2> resolveFilter,
              final RejectFilter<Rejected, Rejected2> rejectFilter,
              final ProgressFilter<Progress, Progress2> progressFilter);

  public  <Resolved2> Promise<Resolved2, Rejected, Progress>
          pipe(final ResolveFilter<Resolved, Resolved2> resolveFilter);

}

