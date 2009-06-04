package net.hillsdon.eclipse.terminator.view.actions;

import net.hillsdon.eclipse.terminator.TerminatorPlugin;
import net.hillsdon.eclipse.terminator.view.Finder;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 
 * @author mth
 */
public class FindPreviousAction extends Action {

  public static final String ID = "net.hillsdon.eclipse.terminator.findPrevious";
  
  private final Finder _finder;
  
  public FindPreviousAction(final Finder finder) {
    _finder = finder;
    setText("Previous");
    setActionDefinitionId(ID);
    setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(TerminatorPlugin.ID, "/icons/find_previous.png"));
  }

  @Override
  public void run() {
    _finder.findPrevious();
  }

}
