package org.codeandmagic.deferredobject.pipe;

/** User: cvrabie1 Date: 10/07/2012 */
public class PassThroughRejectFilter<Reject> implements RejectFilter<Reject,Reject> {
  @Override
  public Reject filterRejected(Reject rejected) {
    return rejected;
  }
}
