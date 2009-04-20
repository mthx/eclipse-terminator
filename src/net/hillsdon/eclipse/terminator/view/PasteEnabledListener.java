package net.hillsdon.eclipse.terminator.view;

/**
 * Always called on the SWT thread.
 * 
 * @author mth
 */
public interface PasteEnabledListener {

  void pasteEnablementChanged(boolean newEnablement);
  
}
