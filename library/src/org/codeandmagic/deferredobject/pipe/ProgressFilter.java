package org.codeandmagic.deferredobject.pipe;

/** User: cvrabie1 Date: 10/07/2012 */
public interface ProgressFilter<Progress1, Progress2>{
  public Progress2 filterProgress(Progress1 progress);
}
