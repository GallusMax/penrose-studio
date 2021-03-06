/**
 * Copyright 2009 Red Hat, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.safehaus.penrose.studio;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchPage;
import org.safehaus.penrose.studio.welcome.WelcomeEditorInput;
import org.safehaus.penrose.studio.welcome.WelcomeEditor;
import org.safehaus.penrose.studio.dialog.ErrorDialog;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class PenroseStudioWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    Logger log = LoggerFactory.getLogger(getClass());

    private PenroseStudioActionBarAdvisor actionBarAdvisor;

    public PenroseStudioWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return actionBarAdvisor = new PenroseStudioActionBarAdvisor(configurer);
    }

    public void openIntro() {
        // log.debug("openIntro");
    }

    public void preWindowOpen() {
        // log.debug("preWindowOpen");

        try {
            IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
            configurer.setInitialSize(new Point(1024, 768));
            configurer.setTitle(PenroseStudio.PRODUCT_NAME);
            //configurer.setShowCoolBar(true);
            configurer.setShowStatusLine(true);
            configurer.setShowProgressIndicator(true);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    public void createWindowContents(Shell shell) {
        // log.debug("createWindowContents");
        super.createWindowContents(shell);

        Point size = new Point(1024, 768);
        //System.out.println("size: "+size);

        Display display = shell.getDisplay();
        Rectangle bounds = display.getBounds();
        //System.out.println("bounds: "+bounds);

        shell.setLocation(bounds.x + (bounds.width - size.x)/2, bounds.y + (bounds.height - size.y)/2);
        shell.setImage(PenroseStudio.getImage(PenroseImage.LOGO));
    }

    public Control createEmptyWindowContents(Composite composite) {
        // log.debug("createEmptyWindowContents");
        return super.createEmptyWindowContents(composite);
    }

    public void postWindowCreate() {
        // log.debug("postWindowCreate");

        try {
            IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
            configurer.setTitle(PenroseStudio.PRODUCT_NAME);

            IWorkbenchWindow window = configurer.getWindow();
            IWorkbenchPage page = window.getActivePage();
            page.openEditor(new WelcomeEditorInput(), WelcomeEditor.class.getName());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ErrorDialog.open(e);
        }
/*
        try {
            penroseStudio.connect();
            penroseStudio.open();

        } catch (Exception e) {
            log.error(e.getMessage(), e);

            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            Shell shell = window.getShell();

            MessageDialog.openError(
                    shell,
                    "ERROR",
                    "Failed opening Penrose Studio.\n"+
                            "See penrose-studio.log for details."
            );
        }
*/
    }

    public void postWindowOpen() {
        // log.debug("postWindowOpen");
    }

    public PenroseStudioActionBarAdvisor getActionBarAdvisor() {
        return actionBarAdvisor;
    }
}
