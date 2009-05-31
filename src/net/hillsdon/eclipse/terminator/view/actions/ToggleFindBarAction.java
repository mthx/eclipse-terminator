package net.hillsdon.eclipse.terminator.view.actions;

import net.hillsdon.eclipse.terminator.view.TerminatorEmbedding;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Copy action.  No enablement for now as there doesn't seem
 * to be any in terminator.
 * 
 * @author mth
 */
public class ToggleFindBarAction extends Action {

  public static final String ID = "net.hillsdon.eclipse.terminator.toggleFindBar";
  
  private final TerminatorEmbedding _embedding;
  
  public ToggleFindBarAction(final IWorkbenchWindow window, final TerminatorEmbedding embedding) {
    _embedding = embedding;
    setText("Find");
    setActionDefinitionId(ID);
  }

  @Override
  public void run() {
    _embedding.toggleFindBar();
  }

}
