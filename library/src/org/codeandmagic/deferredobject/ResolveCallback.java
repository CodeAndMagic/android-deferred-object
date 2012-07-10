package org.codeandmagic.deferredobject;

/** User: cvrabie1 Date: 10/07/2012 */
public interface ResolveCallback<Resolved> {
  public void onResolve(final Resolved resolved);
}
