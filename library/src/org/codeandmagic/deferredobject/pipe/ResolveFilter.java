package org.codeandmagic.deferredobject.pipe;

/** User: cvrabie1 Date: 10/07/2012 */
public interface ResolveFilter<Resolved1,Resolved2>{
  public Resolved2 filterResolved(Resolved1 resolved);
}
