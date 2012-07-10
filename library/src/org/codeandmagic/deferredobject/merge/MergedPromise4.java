package org.codeandmagic.deferredobject.merge;

import org.codeandmagic.deferredobject.AbstractPromise;
import org.codeandmagic.deferredobject.MergedPromiseTracker;
import org.codeandmagic.deferredobject.Promise;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromise4<T extends MergedPromiseResult4<Resolved1,Resolved2, Resolved3, Resolved4>, Resolved1,Resolved2, Resolved3, Resolved4>
  extends AbstractPromise< T, MergedPromiseReject, MergedPromiseProgress>
  implements MergedPromise{

  private final MergedPromiseTracker<T> mergedPromiseTracker;

  public MergedPromise4( final Promise<Resolved1, ?, ?> promise1, final Promise<Resolved2, ?, ?> promise2,
                         final Promise<Resolved3, ?, ?> promise3, final Promise<Resolved4, ?, ?> promise4){
    mergedPromiseTracker = new MergedPromiseTracker<T>(this, promise1, promise2, promise3, promise4);
  }
}
