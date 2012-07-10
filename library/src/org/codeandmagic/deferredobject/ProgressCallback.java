package org.codeandmagic.deferredobject;

/** User: cvrabie1 Date: 10/07/2012 */
public interface ProgressCallback<Progress> {
  public void onProgress(final Progress progress);
}
