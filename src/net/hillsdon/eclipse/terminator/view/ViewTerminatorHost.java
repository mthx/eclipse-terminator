package net.hillsdon.eclipse.terminator.view;

import java.awt.Dimension;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import terminator.view.JTerminalPane;
import terminator.view.TerminalPaneHost;

/**
 * Plays the role of the terminal frame but only hosts a single terminal pane
 * inside an Eclipse view.
 * 
 * @author mth
 */
public class ViewTerminatorHost implements TerminalPaneHost  {

  private final TerminatorView _terminatorView;
  private final EventThreads _eventThreads;

  public ViewTerminatorHost(final TerminatorView terminatorView, final EventThreads eventThreads) {
    _terminatorView = terminatorView;
    _eventThreads = eventThreads;
  }

  @Override
  public void closeTerminalPane(final JTerminalPane terminalPane) {
    _eventThreads.runSWTFromSwing(new Runnable() {
      public void run() {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (activePage.isPartVisible(_terminatorView)) {
          activePage.hideView(_terminatorView);
        }
      }
    });
  }

  @Override
  public boolean confirmClose(String processesUsingTty) {
    return true;
  }

  public void cycleTab(int delta) {
  }

  public boolean isShowingMenu() {
    return false;
  }

  public void moveCurrentTab(int direction) {
  }

  public void setSelectedTabIndex(int index) {
  }

  public void setTerminalSize(Dimension size) {
  }

  public void terminalNameChanged(final JTerminalPane terminalPane) {
    _eventThreads.runSWTFromSwing(new Runnable() {
      public void run() {
        _terminatorView.setPartName(terminalPane.getName());
      }
    });
  }

  public void updateFrameTitle() {
  }
  
}
