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

import org.codeandmagic.deferredobject.merge.*;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromiseTracker<Resolved>{
  private final AbstractPromise<Resolved, MergedPromiseReject, MergedPromiseProgress> mergedPromise;
  private final Promise<?, ?, ?>[] promises;
  private final Object[] results;
  private int done = 0;

  public MergedPromiseTracker(AbstractPromise<Resolved,MergedPromiseReject,MergedPromiseProgress> mergedPromise, Promise<?,?,?>... promises){
    this.mergedPromise = mergedPromise;
    this.promises = promises;
    this.results = new Object[promises.length];
    addListeners();
  }

  private synchronized boolean isDone(){
    return done == promises.length;
  }

  private synchronized void done(int indx, Object result){
    results[indx] = result;
    this.done ++;
    mergedPromise.notify(new MergedPromiseProgress(this.done, promises.length));
    if(isDone()) mergedPromise.resolve(resolve());
  }

  private Resolved resolve() {
    switch (promises.length){
      case 2: return (Resolved) new MergedPromiseResult2(results[0],results[1]);
      case 3: return (Resolved) new MergedPromiseResult3(results[0],results[1],results[2]);
      case 4: return (Resolved) new MergedPromiseResult4(results[0],results[1],results[2],results[3]);
      case 5: return (Resolved) new MergedPromiseResult5(results[0],results[1],results[2],results[3],results[4]);
      default: return (Resolved) results;
    }
  }

  private synchronized void fail(int indx, Object fail){
    mergedPromise.reject(new MergedPromiseReject(indx,fail));
  }

  private synchronized void addListeners(){
    int count = 0;

    for(Promise<?,?,?> p : promises){
      final int indx = count++;
      p.done(new ResolveCallback() {
        @Override
        public void onResolve(Object o) {
          MergedPromiseTracker.this.done(indx,o);
        }
      });
      p.fail(new RejectCallback() {
        @Override
        public void onReject(Object o) {
          MergedPromiseTracker.this.fail(indx,o);
        }
      });

    }
  }
}


