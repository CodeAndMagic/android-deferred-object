package org.codeandmagic.deferredobject.merge;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromiseProgress {
  public final int promisesFulfiled;
  public final int totalPromises;

  public MergedPromiseProgress(final int promisesFulfiled, final int totalPromises){
    this.promisesFulfiled = promisesFulfiled;
    this.totalPromises = totalPromises;
  }
}
