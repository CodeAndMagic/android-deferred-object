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

import org.codeandmagic.deferredobject.pipe.ProgressFilter;
import org.codeandmagic.deferredobject.pipe.RejectFilter;
import org.codeandmagic.deferredobject.pipe.ResolveFilter;
import org.codeandmagic.deferredobject.pipe.ResolvePipe;

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

  public <Resolved2, Rejected2, Progress2> Promise<Resolved2, Rejected2, Progress2>
          pipe(final ResolvePipe<Resolved, Resolved2, Rejected2, Progress2> resolvedPipe);

  public  <Resolved2> Promise<Resolved2, Rejected, Progress>
          pipe(final ResolveFilter<Resolved, Resolved2> resolveFilter);



}

