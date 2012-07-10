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

/** User: cvrabie1 Date: 09/07/2012 */
public class PipedPromise<Resolved1, Rejected1, Progress1, Resolved2, Rejected2, Progress2>
  extends AbstractPromise<Resolved2, Rejected2, Progress2>  {

  protected PipedPromise(final Promise<Resolved1, Rejected1, Progress1> pipedPromise,
                      final ResolveFilter<Resolved1, Resolved2> resolvedFilter,
                      final RejectFilter<Rejected1, Rejected2> rejectedFilter,
                      final ProgressFilter<Progress1, Progress2> progressFilter
  ){
    pipedPromise.done(new ResolveCallback<Resolved1>() {
      @Override
      public void onResolve(Resolved1 resolved1) {
        PipedPromise.this.resolve( resolvedFilter.filterResolved(resolved1) );
      }
    });

    pipedPromise.fail(new RejectCallback<Rejected1>() {
      @Override
      public void onReject(Rejected1 rejected1) {
        PipedPromise.this.reject( rejectedFilter.filterRejected(rejected1) );
      }
    });

    pipedPromise.progress(new ProgressCallback<Progress1>() {
      @Override
      public void onProgress(Progress1 progress1) {
        PipedPromise.this.notify(progressFilter.filterProgress(progress1));
      }
    });
  }
}
