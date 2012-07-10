package org.codeandmagic.deferredobject.pipe;

/** User: cvrabie1 Date: 10/07/2012 */
public class PassThroughResolveFilter<Resolved> implements ResolveFilter<Resolved,Resolved> {
  @Override
  public Resolved filterResolved(Resolved resolved) {
    return resolved;
  }
}
