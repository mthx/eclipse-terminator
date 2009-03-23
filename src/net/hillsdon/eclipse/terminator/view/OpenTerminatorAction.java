package net.hillsdon.eclipse.terminator.view;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;

import java.util.UUID;

import net.hillsdon.eclipse.terminator.TerminatorPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Opens a new terminator (we support multiple instances of the view).
 * 
 * @author mth
 */
public class OpenTerminatorAction extends Action {

  public OpenTerminatorAction() {
    super("&New Terminator View");
    setImageDescriptor(imageDescriptorFromPlugin(TerminatorPlugin.ID, "/icons/terminator-16.png"));
  }

  @Override
  public void run() {
    final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    final String secondaryId = UUID.randomUUID().toString();
    try {
      page.showView(TerminatorView.ID, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
    }
    catch (PartInitException ex) {
    }
  }
  
}
