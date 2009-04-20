package net.hillsdon.eclipse.terminator.view.actions;

import net.hillsdon.eclipse.terminator.view.PasteEnabledListener;
import net.hillsdon.eclipse.terminator.view.TerminatorEmbedding;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Paste action.
 * 
 * @author mth
 */
public class PasteAction extends Action implements PasteEnabledListener {

  public static final String ID = "net.hillsdon.eclipse.terminator.edit.paste";
  
  private final TerminatorEmbedding _embedding;

  public PasteAction(final IWorkbenchWindow window, final TerminatorEmbedding embedding) {
    _embedding = embedding;
    setText("Paste");
    setToolTipText("Paste"); 
    // We don't want the Ctrl-V shortcut that we'd get using the standard paste id.
    setActionDefinitionId(ID);
    ISharedImages sharedImages = window.getWorkbench().getSharedImages();
    setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
    setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));

    _embedding.addPasteEnabledListener(this);
  }

  @Override
  public void run() {
    _embedding.doPasteAction();
  }

  public void pasteEnablementChanged(boolean newEnablement) {
    setEnabled(newEnablement);
  }

}
