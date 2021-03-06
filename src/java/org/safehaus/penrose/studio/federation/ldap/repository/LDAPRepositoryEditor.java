package org.safehaus.penrose.studio.federation.ldap.repository;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.safehaus.penrose.federation.LDAPRepositoryClient;
import org.safehaus.penrose.federation.FederationRepositoryConfig;
import org.safehaus.penrose.federation.FederationClient;
import org.safehaus.penrose.studio.server.Server;
import org.safehaus.penrose.studio.dialog.ErrorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAPRepositoryEditor extends FormEditor {

    public Logger log = LoggerFactory.getLogger(getClass());

    Server server;
    FederationClient federationClient;
    LDAPRepositoryClient ldapFederationClient;
    FederationRepositoryConfig repositoryConfig;

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        LDAPRepositoryEditorInput ei = (LDAPRepositoryEditorInput)input;
        server = ei.getServer();
        federationClient = ei.getFederationClient();
        ldapFederationClient = ei.getLdapFederationClient();
        repositoryConfig = ei.getRepositoryConfig();

        setSite(site);
        setInput(input);
        setPartName(ei.getName());
    }

    public void addPages() {
        try {
            LDAPRepositorySettingsPage page = new LDAPRepositorySettingsPage(this);
            page.setServer(server);
            page.setFederationClient(federationClient);
            page.setLdapFederationClient(ldapFederationClient);
            page.setRepositoryConfig(repositoryConfig);
            addPage(page);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ErrorDialog.open(e);
        }
    }

    public void doSave(IProgressMonitor iProgressMonitor) {
    }

    public void doSaveAs() {
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public FederationRepositoryConfig getRepositoryConfig() {
        return repositoryConfig;
    }

    public void setRepositoryConfig(FederationRepositoryConfig repositoryConfig) {
        this.repositoryConfig = repositoryConfig;
    }

    public LDAPRepositoryClient getLdapFederationClient() {
        return ldapFederationClient;
    }
}
