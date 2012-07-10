package org.codeandmagic.deferredobject.pipe;

/** User: cvrabie1 Date: 10/07/2012 */
public interface RejectFilter<Rejected1, Rejected2>{
  public Rejected2 filterRejected(Rejected1 rejected);
}
