package net.hillsdon.eclipse.terminator.view;

/**
 * 
 * @author mth
 */
public interface FindCallback {

  /**
   * Find status has changed.  Called asynchronously on the SWT thread.
   * 
   * @param text New status text.
   */
  void statusChanged(String text);

}
