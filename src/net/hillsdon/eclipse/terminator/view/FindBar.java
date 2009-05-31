package net.hillsdon.eclipse.terminator.view;

import java.util.regex.PatternSyntaxException;

import net.hillsdon.eclipse.terminator.TerminatorPlugin;
import net.hillsdon.eclipse.terminator.view.actions.FindNextAction;
import net.hillsdon.eclipse.terminator.view.actions.FindPreviousAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Firefox-style search bar.
 * 
 * @author mth
 */
public class FindBar {

  private static final ImageDescriptor CANCEL_IMAGE_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(TerminatorPlugin.ID, "/icons/find_cancel.png");
  
  private final Finder _finder;
  
  private Text _text;
  private Composite _parent;
  private GridData _layoutData;


  public FindBar(final Finder finder) {
    _finder = finder;
  }

  public void install(final Composite parent) {
    _parent = parent;
    ToolBar toolbar = new ToolBar(parent, SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT);
    _layoutData = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1);
    _layoutData.exclude = true;
    toolbar.setLayoutData(_layoutData);
    ToolItem close = new ToolItem(toolbar, SWT.PUSH);
    close.setImage(CANCEL_IMAGE_DESCRIPTOR.createImage());
    close.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent e) {
        toggle();
      }
    });
    close.setToolTipText("Close Find Bar");

    ToolItem labelItem = new ToolItem(toolbar, SWT.SEPARATOR);
    Composite labelComposite = new Composite(toolbar, SWT.NONE);
    labelComposite.setLayout(new GridLayout());
    Label label = new Label(labelComposite, SWT.NONE);
    label.setLayoutData(new GridData(GridData.FILL_BOTH));
    label.setText("Find:");
    labelItem.setControl(labelComposite);
    labelItem.setWidth(labelComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
    
    ToolItem textItem = new ToolItem(toolbar, SWT.SEPARATOR);
    textItem.setWidth(200);
    _text = new Text(toolbar, SWT.SINGLE | SWT.BORDER);
    _text.addSelectionListener(new SelectionAdapter() {
      public void widgetDefaultSelected(final SelectionEvent e) {
        find();
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
    
    addAction(toolbar, new FindPreviousAction(_finder));
    addAction(toolbar, new FindNextAction(_finder));
  }

  private void addAction(final ToolBar toolbar, final IAction action) {
    ActionContributionItem contribution = new ActionContributionItem(action);
    contribution.setMode(ActionContributionItem.MODE_FORCE_TEXT);
    contribution.fill(toolbar, SWT.DEFAULT);
  }

  private void find() {
    String regularExpression = _text.getText();
    try {
      _finder.find(regularExpression);
    }
    catch (PatternSyntaxException ex) {
      // Indicate error somehow.
    }
  }

  public void toggle() {
    _layoutData.exclude = !_layoutData.exclude;
    if (_layoutData.exclude) {
      _text.setText("");
      find();
    }
    else {
      _text.setFocus();
    }
    _parent.layout();
  }
  
}
