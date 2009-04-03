/*******************************************************************************
 * Copyright (c) 2008 Matthew Hillsdon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hillsdon <matt@hillsdon.net>
 *******************************************************************************/
package net.hillsdon.eclipse.terminator.actions;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;

import java.io.File;

import net.hillsdon.eclipse.selection2resource.IResourceSelection;
import net.hillsdon.eclipse.selection2resource.ResourceSelection;
import net.hillsdon.eclipse.terminator.TerminatorPlugin;
import net.hillsdon.eclipse.terminator.view.TerminatorView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Opens a shell with a working directory based on the CWD of
 * the active terminator, or, if there is none the directory
 * containing selected resource..
 *
 * @author mth
 */
public class OpenTerminatorAction extends Action implements IWorkbenchWindowActionDelegate {
  
  private final IResourceSelection _selection;
  private IWorkbenchWindow _window;

  public OpenTerminatorAction() {
    this(new ResourceSelection(PlatformUI.getWorkbench()));
  }

  public OpenTerminatorAction(IResourceSelection selection) {
    super("&New Terminator View");
    _selection = selection;
    setImageDescriptor(imageDescriptorFromPlugin(TerminatorPlugin.ID, "/icons/terminator-16.png"));
  }
  
  public void init(final IWorkbenchWindow window) {
    _window = window;
  }

  public void selectionChanged(final IAction action, final ISelection selection) {
    _selection.setWorkbenchSelection(selection);
    // We're always enabled - we can always open a shell even if it isn't at the ideal location.
  }
  
  public void dispose() {
  }

  public void run(final IAction action) {
    final IWorkbenchPage page = _window.getActivePage();
    final String workingDirectory = getWorkingDirectoryForNewView(page);
    try {
      page.showView(TerminatorView.ID, TerminatorView.createSecondaryId(workingDirectory), IWorkbenchPage.VIEW_ACTIVATE);
    }
    catch (PartInitException ex) {
    }
  }

  String getWorkingDirectoryForNewView(final IWorkbenchPage page) {
    IWorkbenchPart activePart = page.getActivePart();
    if (activePart instanceof TerminatorView) {
      return ((TerminatorView) activePart).getCwdOfTerminalIfPossible();
    }
    File directory = _selection.asDirectory();
    return directory == null ? null : directory.toString();
  }
  
}
