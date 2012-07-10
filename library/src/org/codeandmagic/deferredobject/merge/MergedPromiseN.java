package org.codeandmagic.deferredobject.merge;

import org.codeandmagic.deferredobject.AbstractPromise;
import org.codeandmagic.deferredobject.MergedPromiseTracker;
import org.codeandmagic.deferredobject.Promise;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromiseN extends AbstractPromise<Object[], MergedPromiseReject, MergedPromiseProgress> implements MergedPromise{
  private final MergedPromiseTracker<Object[]> mergedPromiseTracker;

  public MergedPromiseN( final Promise<?, ?, ?>[] promises){
    mergedPromiseTracker = new MergedPromiseTracker<Object[]>(this, promises);
  }
}
