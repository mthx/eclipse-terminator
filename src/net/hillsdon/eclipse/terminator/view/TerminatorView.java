package net.hillsdon.eclipse.terminator.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import terminator.view.JTerminalPane;

/**
 * A view hosting a single terminator pane.
 * 
 * @author mth
 */
public class TerminatorView extends ViewPart {

  public static final String ID = "net.hillsdon.eclipse.terminator.view.TerminatorView";
  
  private TerminatorEmbedding _embedding;

  @Override
  public void init(final IViewSite site) throws PartInitException {
    super.init(site);
    site.getActionBars().getToolBarManager().add(new OpenTerminatorAction());
  }
  
  @Override
  public void createPartControl(final Composite parent) {
    final JTerminalPane terminalPane = JTerminalPane.newShell();
    final EventThreads eventThreads = new EventThreads(parent, terminalPane);
    final ViewTerminatorHost host = new ViewTerminatorHost(this, eventThreads);
    _embedding = new TerminatorEmbedding(terminalPane, host, eventThreads);
    _embedding.install(parent);
  }

  @Override
  public void dispose() {
    _embedding.dispose();
  }

  @Override
  public void setFocus() {
    _embedding.setFocus();
  }
 
  @Override
  public void setPartName(final String partName) {
    super.setPartName(partName);
  }
  
}