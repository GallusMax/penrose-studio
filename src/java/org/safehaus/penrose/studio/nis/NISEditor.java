package org.safehaus.penrose.studio.nis;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.apache.log4j.Logger;
import org.safehaus.penrose.nis.NISDomain;

public class NISEditor extends FormEditor {

    Logger log = Logger.getLogger(getClass());

    NISDomain domain;

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        NISEditorInput ei = (NISEditorInput)input;
        domain = ei.getDomain();

        setSite(site);
        setInput(input);
        setPartName("NIS - "+domain.getName());
    }

    public void addPages() {
        try {
            addPage(new NISDomainPage(this));
            addPage(new NISUsersPage(this));
            addPage(new NISUserChangesPage(this));
            addPage(new NISGroupsPage(this));
            addPage(new NISGroupChangesPage(this));
            addPage(new NISHostsPage(this));
            addPage(new NISFilesPage(this));
            addPage(new NISToolsPage(this));

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
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

    public NISDomain getDomain() {
        return domain;
    }

    public void setDomain(NISDomain domain) {
        this.domain = domain;
    }
}