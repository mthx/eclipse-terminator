package net.hillsdon.eclipse.terminator.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import net.hillsdon.eclipse.terminator.TerminatorPlugin;
import net.hillsdon.eclipse.terminator.view.actions.FindNextAction;
import net.hillsdon.eclipse.terminator.view.actions.FindPreviousAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import terminator.view.highlight.FindStatusDisplay;

/**
 * Firefox-style search bar.
 * 
 * @author mth
 */
public class FindBar {

  private static final ImageDescriptor CANCEL_IMAGE_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(TerminatorPlugin.ID, "/icons/find_cancel.png");
  
  private final Finder _finder;
  private final EventThreads _eventThreads;
  /**
   * Called from Swing thread.
   */
  private final FindStatusDisplay _findStatusDisplay = new FindStatusDisplay() {
    public void setStatus(final String text, final boolean isError) {
      setStatusFromSwing(text);
    }
  };
  
  private final IHandlerService _handlerService;
  private TerminatorEmbedding _terminatorEmbedding;
  private Text _text;
  private Composite _parent;
  private GridData _layoutData;
  private ToolItem _statusLabel;
  private ToolBar _toolbar;
  private Timer _textModifiedTimer;
  private String _textFieldContents = "";

  public FindBar(final Finder finder, final EventThreads eventThreads, final IHandlerService handlerService) {
    _finder = finder;
    _eventThreads = eventThreads;
    _handlerService = handlerService;
    _textModifiedTimer = new Timer(500, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        eventThreads.runSWTFromSwing(new Runnable() {
          public void run() {
            find();
          }
        });
      }
    });
    _textModifiedTimer.setRepeats(false);
  }

  public void install(final TerminatorEmbedding terminatorEmbedding, final Composite parent) {
    _terminatorEmbedding = terminatorEmbedding;
    _parent = parent;
    _toolbar = new ToolBar(parent, SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT);
    _layoutData = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1);
    _layoutData.exclude = true;
    _toolbar.setLayoutData(_layoutData);
    ToolItem close = new ToolItem(_toolbar, SWT.PUSH);
    close.setImage(CANCEL_IMAGE_DESCRIPTOR.createImage());
    close.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent e) {
        toggle();
      }
    });
    close.setToolTipText("Close Find Bar");

    createLabel(_toolbar, "Find:", 5);
    
    ToolItem textItem = new ToolItem(_toolbar, SWT.SEPARATOR);
    textItem.setWidth(200);
    _text = new Text(_toolbar, SWT.SINGLE | SWT.BORDER);
    _text.addSelectionListener(new SelectionAdapter() {
      public void widgetDefaultSelected(final SelectionEvent e) {
        find();
      }
    });
    _text.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        _textFieldContents = _text.getText();
        _textModifiedTimer.restart();
      }
    });
    _text.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.character == SWT.ESC) {
          toggle();
        }
      }
    });
    textItem.setControl(_text);
    
    addAction(_toolbar, new FindPreviousAction(_finder));
    addAction(_toolbar, new FindNextAction(_finder));
    
    _statusLabel = createLabel(_toolbar, "", 25);
  }

  private void setStatusFromSwing(final String text) {
    _eventThreads.runSWTFromSwing(new Runnable() {
      public void run() {
        if (!_statusLabel.isDisposed()) {
          // Ick.  The text doesn't line up anyway so fix this properly!
          ((Label) ((Composite) _statusLabel.getControl()).getChildren()[0]).setText(text);
          sizeToControlWidth(_statusLabel);
        }
      }
    });
  }
  
  private ToolItem createLabel(final ToolBar toolbar, final String text, final int marginLeft) {
    ToolItem labelItem = new ToolItem(toolbar, SWT.SEPARATOR);
    Composite labelComposite = new Composite(toolbar, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginLeft = marginLeft;
    labelComposite.setLayout(layout);
    Label label = new Label(labelComposite, SWT.NONE);
    label.setLayoutData(new GridData(GridData.FILL_BOTH));
    label.setText(text);
    labelItem.setControl(labelComposite);
    sizeToControlWidth(labelItem);
    return labelItem;
  }

  private void sizeToControlWidth(final ToolItem labelItem) {
    labelItem.setWidth(labelItem.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
  }

  private void addAction(final ToolBar toolbar, final IAction action) {
    ActionContributionItem contribution = new ActionContributionItem(action);
    contribution.setMode(ActionContributionItem.MODE_FORCE_TEXT);
    contribution.fill(toolbar, SWT.DEFAULT);
    _handlerService.activateHandler(action.getActionDefinitionId(), new ActionHandler(action));
  }

  private void find() {
    // We're called from a Timer.
    if (!_parent.isDisposed()) {
      _finder.find(_textFieldContents, _findStatusDisplay);
    }
  }

  public void toggle() {
    _layoutData.exclude = !_layoutData.exclude;
    if (_layoutData.exclude) {
      _textModifiedTimer.stop();
      _text.setText("");
      _terminatorEmbedding.setFocus();
      find();
    }
    else {
      _text.setFocus();
      _textModifiedTimer.start();
    }
    _parent.layout();
  }

  public void dispose() {
    if (_textModifiedTimer.isRunning()) {
      _textModifiedTimer.stop();
    }
  }
  
}
