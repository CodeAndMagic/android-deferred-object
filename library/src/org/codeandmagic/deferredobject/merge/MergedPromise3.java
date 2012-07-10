package org.codeandmagic.deferredobject.merge;

import org.codeandmagic.deferredobject.AbstractPromise;
import org.codeandmagic.deferredobject.MergedPromiseTracker;
import org.codeandmagic.deferredobject.Promise;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromise3<T extends MergedPromiseResult3<Resolved1,Resolved2, Resolved3>, Resolved1,Resolved2, Resolved3>
  extends AbstractPromise< T, MergedPromiseReject, MergedPromiseProgress>
  implements MergedPromise{

  private final MergedPromiseTracker<T> mergedPromiseTracker;

  public MergedPromise3( final Promise<Resolved1, ?, ?> promise1, final Promise<Resolved2, ?, ?> promise2, final Promise<Resolved3, ?, ?> promise3){
    mergedPromiseTracker = new MergedPromiseTracker<T>(this,promise1,promise2,promise3);
  }
}
