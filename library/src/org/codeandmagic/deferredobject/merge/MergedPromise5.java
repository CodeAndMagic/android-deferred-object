package org.codeandmagic.deferredobject.merge;

import org.codeandmagic.deferredobject.AbstractPromise;
import org.codeandmagic.deferredobject.MergedPromiseTracker;
import org.codeandmagic.deferredobject.Promise;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromise5<T extends MergedPromiseResult5<Resolved1,Resolved2, Resolved3, Resolved4, Resolved5>,
  Resolved1,Resolved2, Resolved3, Resolved4, Resolved5>
  extends AbstractPromise< T, MergedPromiseReject, MergedPromiseProgress>
  implements MergedPromise{

  private final MergedPromiseTracker<T> mergedPromiseTracker;

  public MergedPromise5( final Promise<Resolved1, ?, ?> promise1, final Promise<Resolved2, ?, ?> promise2,
                         final Promise<Resolved3, ?, ?> promise3, final Promise<Resolved4, ?, ?> promise4, final Promise<Resolved5, ?, ?> promise5){
    mergedPromiseTracker = new MergedPromiseTracker<T>(this, promise1, promise2, promise3, promise4, promise5);
  }
}
