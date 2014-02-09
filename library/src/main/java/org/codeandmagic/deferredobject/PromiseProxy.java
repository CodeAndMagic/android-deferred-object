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

/** User: cvrabie1 Date: 12/07/2012 */
public class PromiseProxy<Resolved,Rejected,Progress> extends AbstractPromise<Resolved,Rejected,Progress>{

  private Promise<Resolved,Rejected,Progress> proxiedPromise = null;

  public Promise<Resolved, Rejected, Progress> getProxiedPromise() {
    return proxiedPromise;
  }

  public void setProxiedPromise(Promise<Resolved, Rejected, Progress> proxiedPromise) {
    this.proxiedPromise = proxiedPromise;

    proxiedPromise.then(new ResolveCallback<Resolved>() {
      @Override
      public void onResolve(Resolved resolved) {
        PromiseProxy.this.resolve(resolved);
      }
    }, new RejectCallback<Rejected>() {
      @Override
      public void onReject(Rejected rejected) {
        PromiseProxy.this.reject(rejected);
      }
    }, new ProgressCallback<Progress>() {
      @Override
      public void onProgress(Progress progress) {
        PromiseProxy.this.notify(progress);
      }
    });
  }
}
