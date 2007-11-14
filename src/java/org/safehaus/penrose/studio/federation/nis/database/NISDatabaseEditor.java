package org.safehaus.penrose.studio.federation.nis.database;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.safehaus.penrose.studio.federation.nis.NISFederation;
import org.safehaus.penrose.studio.federation.nis.NISDomain;
import org.safehaus.penrose.studio.project.Project;

public class NISDatabaseEditor extends FormEditor {

    public Logger log = LoggerFactory.getLogger(getClass());

    NISFederation nisFederation;
    NISDomain domain;
    private Project project;

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        NISDatabaseEditorInput ei = (NISDatabaseEditorInput)input;
        nisFederation = ei.getNisFederation();
        domain = ei.getDomain();
        project = ei.getProject();

        setSite(site);
        setInput(input);
        setPartName(ei.getName());
    }

    public void addPages() {
        try {
            addPage(new NISDatabaseCachePage(this));
            addPage(new NISDatabaseChangeLogPage(this));
            addPage(new NISDatabaseTablesPage(this));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

    public NISFederation getNisTool() {
        return nisFederation;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
