package org.safehaus.penrose.studio.source.editor;

import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.IManagedForm;
import org.apache.log4j.Logger;
import org.safehaus.penrose.partition.PartitionConfig;
import org.safehaus.penrose.source.SourceConfig;
import org.safehaus.penrose.studio.project.ProjectNode;

/**
 * @author Endi Sukma Dewata
 */
public class SourceEditorPage extends FormPage {

    public Logger log = Logger.getLogger(getClass());

    protected FormToolkit toolkit;

    protected SourceEditor editor;

    protected ProjectNode projectNode;
    protected PartitionConfig partitionConfig;
    protected SourceConfig sourceConfig;

    public SourceEditorPage(SourceEditor editor, String name, String label) {
        super(editor, name, label);

        this.editor = editor;

        projectNode = editor.getProjectNode();
        partitionConfig = editor.getPartitionConfig();
        sourceConfig = editor.getSourceConfig();
    }

    public void createFormContent(IManagedForm managedForm) {

        toolkit = managedForm.getToolkit();

        ScrolledForm form = managedForm.getForm();
        form.setText(getTitle());
    }

    public void setActive(boolean b) {
        super.setActive(b);
        if (b) refresh();
    }

    public void refresh() {
    }

    public FormToolkit getToolkit() {
        return toolkit;
    }

    public void setToolkit(FormToolkit toolkit) {
        this.toolkit = toolkit;
    }

    public PartitionConfig getPartitionConfig() {
        return partitionConfig;
    }

    public void setPartitionConfig(PartitionConfig partitionConfig) {
        this.partitionConfig = partitionConfig;
    }

    public ProjectNode getProjectNode() {
        return projectNode;
    }

    public void setProjectNode(ProjectNode projectNode) {
        this.projectNode = projectNode;
    }

    public SourceConfig getSourceConfig() {
        return sourceConfig;
    }

    public void setSourceConfig(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public void checkDirty() {
        editor.checkDirty();
    }

    public void setDirty(boolean dirty) {
        editor.setDirty(dirty);
    }
}