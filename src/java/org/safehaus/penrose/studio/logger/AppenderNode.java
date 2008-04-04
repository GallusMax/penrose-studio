package org.safehaus.penrose.studio.logger;

import org.safehaus.penrose.studio.tree.Node;
import org.safehaus.penrose.studio.server.ServersView;
import org.safehaus.penrose.studio.PenroseStudioPlugin;
import org.safehaus.penrose.studio.PenroseImage;
import org.safehaus.penrose.studio.PenroseStudio;
import org.safehaus.penrose.logger.log4j.AppenderConfig;
import org.safehaus.penrose.logger.log4j.Log4jConfig;
import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Action;

/**
 * @author Endi S. Dewata
 */
public class AppenderNode extends Node {

    Logger log = Logger.getLogger(getClass());

    ServersView view;
    AppenderConfig appenderConfig;

    public AppenderNode(ServersView view, String name, Image image, Object object, Object parent) {
        super(name, image, object, parent);
        this.view = view;
        this.appenderConfig = (AppenderConfig)object;
    }

    public void showMenu(IMenuManager manager) {

        manager.add(new Action("Open") {
            public void run() {
                try {
                    open();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        });

        manager.add(new Action("Delete", PenroseStudioPlugin.getImageDescriptor(PenroseImage.SIZE_16x16, PenroseImage.DELETE)) {
            public void run() {
                try {
                    remove();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        });
    }

    public void open() throws Exception {

        AppenderDialog dialog = new AppenderDialog(view.getSite().getShell(), SWT.NONE);
        dialog.setText("Edit Appender");
        dialog.setAppenderConfig(appenderConfig);
        dialog.open();
    }

    public void remove() throws Exception {
        PenroseStudio penroseStudio = PenroseStudio.getInstance();
        Log4jConfig loggingConfig = penroseStudio.getLoggingConfig();
        loggingConfig.removeAppenderConfig(appenderConfig.getName());
        penroseStudio.notifyChangeListeners();
    }
}