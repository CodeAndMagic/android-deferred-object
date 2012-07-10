/*
 * Copyright 2012 Cristian Vrabie, Evelina Vrabie
 *
 * This file is part of android-deferred-object.
 *
 * android-deferred-object is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * android-deferred-object is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.codeandmagic.deferredobject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.codeandmagic.deferredobject.pipe.PassThroughProgressFilter;
import org.codeandmagic.deferredobject.pipe.PassThroughRejectFilter;
import org.codeandmagic.deferredobject.pipe.ProgressFilter;
import org.codeandmagic.deferredobject.pipe.RejectFilter;
import org.codeandmagic.deferredobject.pipe.ResolveFilter;

/** User: cvrabie1 Date: 10/07/2012 */
public class AbstractPromise<Resolved, Rejected, Progress> implements Promise<Resolved, Rejected, Progress> {

  /*
  * The state of this Deferred Object
  */
	private Promise.State state = State.PENDING;
  /**
   * The value or this deferred object if it has been resolved or null otherwise
   */
  private Resolved resolved;
  /**
   * The rejection reason of this deferred object if it has been rejected or null otherwise
   */
  private Rejected rejected;

  protected final List<ResolveCallback<Resolved>> resolveCallbacks = new CopyOnWriteArrayList<ResolveCallback<Resolved>>();
  protected final List<RejectCallback<Rejected>> rejectedCallbacks = new CopyOnWriteArrayList<RejectCallback<Rejected>>();
  protected final List<ProgressCallback<Progress>> progressCallbacks = new CopyOnWriteArrayList<ProgressCallback<Progress>>();
  protected final List<CompleteCallback<Resolved,Rejected>> completeCallbacks = new CopyOnWriteArrayList<CompleteCallback<Resolved,Rejected>>();

  @Override
  public Promise.State getState() {
    return state;
  }

  @Override
  public boolean isPending() {
    return Promise.State.PENDING == state;
  }

  @Override
  public boolean isRejected() {
    return Promise.State.REJECTED == state;
  }

  @Override
  public boolean isResolved() {
    return Promise.State.RESOLVED == state;
  }

  protected final void tryResolvedTrigger(){
    if(isResolved()) triggerResolved();
  }

  protected final void triggerResolved(){
    triggerCompleted();
    for(final ResolveCallback<Resolved> r : resolveCallbacks){
      r.onResolve(resolved);
    }
  }

  protected void resolve(final Resolved resolved){
    this.resolved = resolved;
    this.state = Promise.State.RESOLVED;
    triggerResolved();
  }

  protected final void tryRejectedTrigger(){
    if(isRejected()) triggerRejected();
  }

  protected final void triggerRejected(){
    triggerCompleted();
    for(final RejectCallback<Rejected> r : rejectedCallbacks){
      r.onReject(rejected);
    }
  }

  protected void reject(final Rejected rejected){
    this.rejected = rejected;
    this.state = Promise.State.REJECTED;
    triggerRejected();
  }

  protected final void tryCompleteTrigger(){
    if(Promise.State.PENDING.compareTo(state) < 0) triggerCompleted();
  }

  protected final void triggerCompleted(){
    for(final CompleteCallback<Resolved,Rejected> c : completeCallbacks){
      c.onComplete( isResolved() ? resolved : null, isRejected() ? rejected : null );
    }
  }

  protected void notify(final Progress progress){
    if(Promise.State.PENDING.compareTo(state) < 0) return;
    for(final ProgressCallback<Progress> p : progressCallbacks){
      p.onProgress(progress);
    }
  }

  @Override
  public Promise<Resolved, Rejected, Progress> then(ResolveCallback<Resolved> onResolve,
                                                    RejectCallback<Rejected> onReject,
                                                    ProgressCallback<Progress> onProgress,
                                                    CompleteCallback<Resolved, Rejected> onComplete) {
    if(onResolve != null) resolveCallbacks.add(onResolve);
    if(onReject != null) rejectedCallbacks.add(onReject);
    if(onProgress != null) progressCallbacks.add(onProgress);
    if(onComplete != null) completeCallbacks.add(onComplete);

    if(onComplete != null) tryCompleteTrigger();
    if(onResolve != null) tryResolvedTrigger();
    if(onReject != null) tryRejectedTrigger();

    return this;
  }

  @Override
  public Promise<Resolved, Rejected, Progress> then(ResolveCallback<Resolved> onResolve,
                                                    RejectCallback<Rejected> onReject,
                                                    ProgressCallback<Progress> onProgress) {
    return then(onResolve, onReject, onProgress, null);
  }

  @Override
  public Promise<Resolved, Rejected, Progress> then(ResolveCallback<Resolved> onResolve) {
    return then(onResolve, null, null);
  }

  @Override
  public Promise<Resolved, Rejected, Progress> then(ResolveCallback<Resolved> onResolve,
                                                    RejectCallback<Rejected> onReject) {
    return then(onResolve, onReject, null);
  }

  @Override
  public Promise<Resolved, Rejected, Progress> done(ResolveCallback<Resolved> onResolve) {
    return then(onResolve);
  }

  @Override
  public Promise<Resolved, Rejected, Progress> fail(RejectCallback<Rejected> onReject) {
    return then(null, onReject);
  }

  @Override
  public Promise<Resolved, Rejected, Progress> progress(ProgressCallback<Progress> onProgress) {
    return then(null, null, onProgress);
  }

  @Override
  public Promise<Resolved, Rejected, Progress> always(CompleteCallback<Resolved,Rejected> onComplete) {
    return then(null, null, null, onComplete);
  }

  public Promise<Resolved, Rejected, Progress> promise(){
    return this;
  }

  @Override
  public <Resolved2, Rejected2, Progress2> Promise<Resolved2, Rejected2, Progress2>
  pipe(final ResolveFilter<Resolved, Resolved2> resolveFilter, final RejectFilter<Rejected, Rejected2> rejectFilter, final ProgressFilter<Progress, Progress2> progressFilter){
    return new PipedPromise<Resolved, Rejected, Progress, Resolved2, Rejected2, Progress2>(
      this, resolveFilter, rejectFilter, progressFilter
    );
  }

  @Override
  public <Resolved2> Promise<Resolved2, Rejected, Progress> pipe(ResolveFilter<Resolved, Resolved2> resolvedFilter) {
    return pipe(resolvedFilter, new PassThroughRejectFilter<Rejected>(), new PassThroughProgressFilter<Progress>());
  }


}
