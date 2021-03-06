package org.safehaus.penrose.studio.federation.nis;

import org.safehaus.penrose.studio.tree.Node;
import org.safehaus.penrose.studio.PenroseStudio;
import org.safehaus.penrose.studio.PenroseImage;
import org.safehaus.penrose.studio.dialog.ErrorDialog;
import org.safehaus.penrose.studio.action.RefreshAction;
import org.safehaus.penrose.studio.server.Server;
import org.safehaus.penrose.federation.NISRepositoryClient;
import org.safehaus.penrose.federation.*;
import org.safehaus.penrose.studio.federation.nis.editor.NISEditorInput;
import org.safehaus.penrose.studio.federation.nis.editor.NISEditor;
import org.safehaus.penrose.studio.federation.nis.domain.NISDomainNode;
import org.safehaus.penrose.studio.federation.nis.wizard.AddNISDomainWizard;
import org.safehaus.penrose.studio.federation.FederationDomainNode;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * @author Endi S. Dewata
 */
public class NISNode extends Node {

    Server server;
    FederationClient federationClient;

    public NISNode(String name, Node parent) throws Exception {
        super(name, PenroseStudio.getImage(PenroseImage.FOLDER), null, parent);
    }

    public void init() throws Exception {
        update();
    }

    public void update() throws Exception {
        log.debug("NIS repositories:");

        for (FederationRepositoryConfig repositoryConfig : getFederationClient().getRepositories("NIS")) {

            String repositoryName = repositoryConfig.getName();
            log.debug(" - "+repositoryName);

            NISRepositoryClient nisFederationClient = new NISRepositoryClient(getFederationClient(), repositoryName);

            NISDomainNode domainNode = new NISDomainNode(repositoryConfig.getName(), this);

            domainNode.setServer(server);
            domainNode.setFederationClient(getFederationClient());
            domainNode.setNisFederationClient(nisFederationClient);
            domainNode.setRepositoryConfig(repositoryConfig);
            domainNode.init();

            addChild(domainNode);
        }
    }

    public void refresh() throws Exception {
        removeChildren();
        update();
    }

    public void showMenu(IMenuManager manager) throws Exception {

        manager.add(new Action("Open") {
            public void run() {
                try {
                    open();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    ErrorDialog.open(e);
                }
            }
        });

        manager.add(new Action("New NIS Repository...") {
            public void run() {
                try {
                    addNisDomain();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    ErrorDialog.open(e);
                }
            }
        });

        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        manager.add(new RefreshAction(this));
    }

    public void open() throws Exception {

        NISEditorInput ei = new NISEditorInput();
        ei.setServer(server);
        ei.setFederationClient(getFederationClient());

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        page.openEditor(ei, NISEditor.class.getName());
    }

    public void addNisDomain() throws Exception {

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        AddNISDomainWizard wizard = new AddNISDomainWizard();
        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.setPageSize(600, 300);
        int rc = dialog.open();

        if (rc == Window.CANCEL) return;

        FederationRepositoryConfig domain = wizard.getRepository();

        FederationClient federationClient = getFederationClient();
        federationClient.addRepository(domain);
        federationClient.store();
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public FederationClient getFederationClient() throws Exception {
        return ((FederationDomainNode)parent).getFederationClient();
    }
}
