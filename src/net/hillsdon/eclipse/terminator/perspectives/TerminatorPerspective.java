/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.hillsdon.eclipse.terminator.perspectives;

import net.hillsdon.eclipse.terminator.view.TerminatorView;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * A perspective without the editor area for organizing terminals.
 * 
 * Optional - you can use the terminal view fine in other perspectives.
 */
public class TerminatorPerspective implements IPerspectiveFactory {

	public void createInitialLayout(final IPageLayout factory) {
	  factory.setEditorAreaVisible(false);
	  factory.addShowViewShortcut(TerminatorView.ID);
	  factory.addView(TerminatorView.ID, IPageLayout.LEFT, 0, null);
	}

}
