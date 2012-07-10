package org.codeandmagic.deferredobject.merge;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromiseReject{
  public final int index;
  public final Object rejected;
  public MergedPromiseReject(final int index, final Object rejected){
    this.index = index;
    this.rejected = rejected;
  }
}
