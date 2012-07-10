package org.codeandmagic.deferredobject.merge;

import org.codeandmagic.deferredobject.AbstractPromise;
import org.codeandmagic.deferredobject.MergedPromiseTracker;
import org.codeandmagic.deferredobject.Promise;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromise2<T extends MergedPromiseResult2<Resolved1,Resolved2>, Resolved1,Resolved2>
  extends AbstractPromise< T, MergedPromiseReject, MergedPromiseProgress>
  implements MergedPromise{

  private final MergedPromiseTracker<T> mergedPromiseTracker;

  public MergedPromise2( final Promise<Resolved1, ?, ?> promise1, final Promise<Resolved2, ?, ?> promise2){
    mergedPromiseTracker = new MergedPromiseTracker<T>(this,promise1,promise2);
  }
}

