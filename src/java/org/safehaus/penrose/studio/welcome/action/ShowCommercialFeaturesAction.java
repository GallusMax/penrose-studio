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
package org.safehaus.penrose.studio.welcome.action;

import org.eclipse.jface.action.Action;
import org.apache.log4j.Logger;
import org.safehaus.penrose.studio.PenroseStudio;
import org.safehaus.penrose.studio.PenroseWorkbenchAdvisor;
import org.safehaus.penrose.studio.PenroseWorkbenchWindowAdvisor;
import org.safehaus.penrose.studio.PenroseActionBarAdvisor;

/**
 * @author Endi S. Dewata
 */
public class ShowCommercialFeaturesAction extends Action {

    Logger log = Logger.getLogger(getClass());

    public ShowCommercialFeaturesAction() {
        setText("&Show Commercial Features");
        setId(getClass().getName());
        setChecked(true);
    }

	public void run() {
        try {
            PenroseStudio penroseStudio = PenroseStudio.getInstance();
            PenroseWorkbenchAdvisor workbenchAdvisor = penroseStudio.getWorkbenchAdvisor();
            PenroseWorkbenchWindowAdvisor workbenchWindowAdvisor = workbenchAdvisor.getWorkbenchWindowAdvisor();
            PenroseActionBarAdvisor actionBarAdvisor = workbenchWindowAdvisor.getActionBarAdvisor();

            actionBarAdvisor.fillPartitionMenu();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
	}
}