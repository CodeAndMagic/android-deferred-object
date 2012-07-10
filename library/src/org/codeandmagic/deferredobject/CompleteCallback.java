package org.codeandmagic.deferredobject;

/** User: cvrabie1 Date: 10/07/2012 */
public interface CompleteCallback<Resolved, Rejected> {
  public void onComplete(final Resolved resolved, final Rejected rejected);
}
