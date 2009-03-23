package net.hillsdon.eclipse.terminator.view;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import terminator.view.JTerminalPane;
import terminator.view.TerminalPaneHost;

/**
 * A view hosting a single terminator pane.
 * 
 * @author mth
 */
public class TerminatorView extends ViewPart implements TerminalPaneHost {

  private JTerminalPane _terminal;
  private Display _display;
  private Composite _embedding;

  @Override
  public void createPartControl(Composite parent) {
    parent.setLayout(new FillLayout());

    _display = parent.getDisplay();
    _embedding = new Composite(parent, SWT.NO_BACKGROUND | SWT.EMBEDDED);
    final Frame frame = SWT_AWT.new_Frame(_embedding);
    // This is probably too strong but <tab> can't be our focus traversal
    // key else tab completion in the shell loses the focus.
    frame.setFocusTraversalKeysEnabled(false);
    _terminal = JTerminalPane.newShell();
    _terminal.setPopupMenuEnabled(false);
    _terminal.setHost(this);
    frame.add(_terminal);
    _terminal.start();

    _embedding.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        runSwingFromSWT(new Runnable() {
          public void run() {
            _terminal.requestFocus();
          }
        });
      }
    });
  }

  @Override
  public void dispose() {
    runSwingFromSWT(new Runnable() {
      @Override
      public void run() {
        _terminal.doCloseAction();
      }
    });
  }

  public void terminalNameChanged(JTerminalPane terminalPane) {
    updateFrameTitle();
  }

  public void updateFrameTitle() {
    runSWTFromSwing(new Runnable() {
      public void run() {
        setPartName(_terminal.getName());
      }
    });
  }

  @Override
  public void setFocus() {
    _embedding.setFocus();
  }

  @Override
  public void closeTerminalPane(JTerminalPane terminalPane) {
    runSWTFromSwing(new Runnable() {
      public void run() {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (activePage.isPartVisible(TerminatorView.this)) {
          activePage.hideView(TerminatorView.this);
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

  private void runSWTFromSwing(final Runnable runnable) {
    _display.asyncExec(new Runnable() {
      public void run() {
        if (!_embedding.isDisposed()) {
          runnable.run();
        }
      }
    });
  }
  
  private void runSwingFromSWT(final Runnable runnable) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        if (_terminal.isVisible() /* Correct check? */) {
          runnable.run();
        }
      }
    });
  }
  
}