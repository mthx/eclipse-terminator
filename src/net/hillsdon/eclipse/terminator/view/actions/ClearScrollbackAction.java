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
public class ClearScrollbackAction extends Action {

  public static final String ID = "net.hillsdon.eclipse.terminator.edit.clearScrollback";
  
  private final TerminatorEmbedding _embedding;
  
  public ClearScrollbackAction(final IWorkbenchWindow window, final TerminatorEmbedding embedding) {
    _embedding = embedding;
    setText("Clear Scrollback");
    setActionDefinitionId(ID);
  }

  @Override
  public void run() {
    _embedding.clearScrollback();
  }

}
