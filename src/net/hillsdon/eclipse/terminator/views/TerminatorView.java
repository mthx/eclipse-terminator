package net.hillsdon.eclipse.terminator.views;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
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

  private Composite _embedding;

  @Override
  public void createPartControl(Composite parent) {
    parent.setLayout(new FillLayout());

    _embedding = new Composite(parent, SWT.EMBEDDED);
    final Frame frame = SWT_AWT.new_Frame(_embedding);
    // This is probably too strong but <tab> can't be our focus traversal
    // key else tab completion in the shell loses the focus.
    frame.setFocusTraversalKeysEnabled(false);
    _terminal = JTerminalPane.newShell();
    _terminal.setHost(this);
    frame.add(_terminal);
    _terminal.start();

    _embedding.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            _terminal.requestFocus();
          }
        });
      }
    });
  }

  @Override
  public void dispose() {
    _terminal.doCloseAction();
  }

  public void terminalNameChanged(JTerminalPane terminalPane) {
    setPartName(terminalPane.getName());
  }

  public void updateFrameTitle() {
  }

  @Override
  public void setFocus() {
    _embedding.setFocus();
  }

  @Override
  public void closeTerminalPane(JTerminalPane terminalPane) {
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

}