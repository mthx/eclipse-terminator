package net.hillsdon.eclipse.terminator.view.actions;

import net.hillsdon.eclipse.terminator.TerminatorPlugin;
import net.hillsdon.eclipse.terminator.view.Finder;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 
 * @author mth
 */
public class FindNextAction extends Action {

  public static final String ID = "net.hillsdon.eclipse.terminator.findNext";
  
  private final Finder _finder;
  
  public FindNextAction(final Finder finder) {
    _finder = finder;
    setText("Next");
    setActionDefinitionId(ID);
    setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(TerminatorPlugin.ID, "/icons/find_next.png"));
  }

  @Override
  public void run() {
    _finder.findNext();
  }

}
