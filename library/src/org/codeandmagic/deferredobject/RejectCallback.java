package org.codeandmagic.deferredobject;

/** User: cvrabie1 Date: 10/07/2012 */
public interface RejectCallback<Rejected> {
  public void onReject(final Rejected rejected);
}