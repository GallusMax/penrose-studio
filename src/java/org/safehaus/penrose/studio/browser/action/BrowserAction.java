/**
 * Copyright (c) 2000-2006, Identyx Corporation.
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
package org.safehaus.penrose.studio.browser.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbenchPage;
import org.safehaus.penrose.studio.PenroseStudioPlugin;
import org.safehaus.penrose.studio.PenroseImage;
import org.safehaus.penrose.studio.dialog.ErrorDialog;
import org.safehaus.penrose.studio.server.ServersView;
import org.safehaus.penrose.studio.project.ProjectConfig;
import org.safehaus.penrose.studio.project.ProjectNode;
import org.safehaus.penrose.studio.project.Project;
import org.safehaus.penrose.studio.browser.BrowserEditorInput;
import org.safehaus.penrose.studio.browser.BrowserEditor;
import org.safehaus.penrose.service.ServiceConfig;
import org.safehaus.penrose.service.ServiceConfigs;
import org.safehaus.penrose.config.PenroseConfig;
import org.apache.log4j.Logger;

/**
 * @author Endi S. Dewata
 */
public class BrowserAction extends Action {

    Logger log = Logger.getLogger(getClass());

    public final static String LDAP_PORT             = "ldapPort";
    public final static int DEFAULT_LDAP_PORT        = 10389;

    public BrowserAction() {

        setText("&Browser");
        setImageDescriptor(PenroseStudioPlugin.getImageDescriptor(PenroseImage.BROWSER));
        setToolTipText("Browser");
        setId(getClass().getName());
    }

	public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        
        try {
            ServersView serversView = ServersView.getInstance();
            ProjectNode projectNode = serversView.getSelectedProjectNode();
            Project project = projectNode.getProject();

            ProjectConfig projectConfig = project.getProjectConfig();
            String hostname = projectConfig.getHost();

            ServiceConfigs serviceConfigs = project.getServiceConfigs();
            ServiceConfig serviceConfig = serviceConfigs.getServiceConfig("LDAP");
            String s = serviceConfig == null ? null : serviceConfig.getParameter(LDAP_PORT);
            int port = s == null ? DEFAULT_LDAP_PORT : Integer.parseInt(s);

            PenroseConfig penroseConfig = project.getPenroseConfig();
            String bindDn = penroseConfig.getRootDn().toString();
            byte[] password = penroseConfig.getRootPassword();

            BrowserEditorInput ei = new BrowserEditorInput();
            ei.setHostname(hostname);
            ei.setPort(port);
            ei.setBindDn(bindDn);
            ei.setPassword(password);

            IWorkbenchPage page = window.getActivePage();
            page.openEditor(ei, BrowserEditor.class.getName());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ErrorDialog.open(e);
        }
	}
}