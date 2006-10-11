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
package org.safehaus.penrose.studio.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.apache.log4j.Logger;

public class Helper {

    static Logger log = Logger.getLogger(Helper.class);

    public static String replace(String string, Map values) {

        StringBuffer sb = new StringBuffer(string);
        int i = sb.indexOf("${");
        while (i >= 0) {
            int j = sb.indexOf("}", i+2);
            String name = sb.substring(i+2, j);
            String value = (String)values.get(name);
            if (value == null) continue;

            sb.replace(i, j+1, value);

            i = sb.indexOf("${", i);
        }

        return sb.toString();
    }

	public static void testJdbcConnection(Shell shell, String driver, String url, String username, String password) {
		java.sql.Connection con = null;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			MessageDialog.openInformation(shell, "Test Connection Result", "Connection successful!");
		} catch (ClassNotFoundException ex) {
            log.debug(ex.getMessage(), ex);
			MessageDialog.openError(shell, "Test Connection Result", "Error: "+ex.getMessage());
		} catch (SQLException ex) {
            log.debug(ex.getMessage(), ex);
			String message = ex.toString();
			if (message.length() > 500) {
				message = message.substring(0, 500) + "...";
			}
			MessageDialog.openError(shell, "Test Connection Result", "Error: "+ex.getSQLState()+"\n"+message);
		} finally {
			try { con.close(); } catch (Exception ex) {}
		}
	}
	
	public static void testJndiConnection(Shell shell, String initialContext, String providerUrl, String principal, String credentials) {
		InitialDirContext ic = null;
		try {
			Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, initialContext);
			env.put(Context.PROVIDER_URL, providerUrl);
			env.put(Context.SECURITY_PRINCIPAL, principal);
			env.put(Context.SECURITY_CREDENTIALS, credentials);
			ic = new InitialDirContext(env);
			MessageDialog.openInformation(shell, "Test Connection Result", "Connection successful!");
		} catch (NamingException ex) {
            log.debug(ex.getMessage(), ex);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String message = sw.toString();
			if (message.length() > 500) {
				message = message.substring(0, 500) + "...";
			}
			MessageDialog.openError(shell, "Test Connection Result", "Error: "+message);
		} finally {
			try { ic.close(); } catch (Exception ex) {} 
		}
	}

	public static void hookContextMenu(Control control, IMenuListener menuListener) {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(menuListener);
		Menu menu = menuMgr.createContextMenu(control);
		control.setMenu(menu);
	}

}