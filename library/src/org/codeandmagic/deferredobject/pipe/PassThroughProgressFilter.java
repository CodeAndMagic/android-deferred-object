package org.codeandmagic.deferredobject.pipe;

/** User: cvrabie1 Date: 10/07/2012 */
public class PassThroughProgressFilter<Progress> implements ProgressFilter<Progress,Progress> {
  @Override
  public Progress filterProgress(Progress progress) {
    return progress;
  }
}
