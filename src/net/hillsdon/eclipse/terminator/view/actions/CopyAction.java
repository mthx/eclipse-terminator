package net.hillsdon.eclipse.terminator.view.actions;

import net.hillsdon.eclipse.terminator.view.TerminatorEmbedding;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Copy action.  No enablement for now as there doesn't seem
 * to be any in terminator.
 * 
 * @author mth
 */
public class CopyAction extends Action {

  public static final String ID = "net.hillsdon.eclipse.terminator.edit.copy";
  
  private final TerminatorEmbedding _embedding;
  
  public CopyAction(final IWorkbenchWindow window, final TerminatorEmbedding embedding) {
    _embedding = embedding;
    setText("Copy");
    // We don't want the Ctrl-C shortcut that we'd get using the standard copy id.
    setActionDefinitionId(ID);
    ISharedImages sharedImages = window.getWorkbench().getSharedImages();
    setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
    setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
  }

  @Override
  public void run() {
    _embedding.copy();
  }

}
