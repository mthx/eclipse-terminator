package net.hillsdon.eclipse.terminator.view;

import java.awt.Component;
import java.awt.EventQueue;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * Async. dispatch between Swing and SWT with guards to abort late event handling.
 * 
 * Normally async calls with guard failure are ignored though Swing calls can be
 * forced if they don't require the terminal pane to be showing. 
 * 
 * @author mth
 */
public class EventThreads {

  private final Display _display;
  private final Widget _widget;
  private final Component _component;

  public EventThreads(final Widget widget, final Component component) {
    _component = component;
    _display = widget.getDisplay();
    _widget = widget;
  }
  
  public void runSWTFromSwing(final Runnable runnable) {
    _display.asyncExec(new Runnable() {
      public void run() {
        if (!_widget.isDisposed()) {
          runnable.run();
        }
      }
    });
  }

  public void runSwingFromSWT(final Runnable runnable) {
    runSwingFromSWT(false, runnable);
  }
  
  public void runSwingFromSWT(final boolean force, final Runnable runnable) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        if (force || _component.isShowing()) {
          runnable.run();
        }
      }
    });
  }

}
