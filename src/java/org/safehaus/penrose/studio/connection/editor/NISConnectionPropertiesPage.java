package org.safehaus.penrose.studio.connection.editor;

import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.apache.log4j.Logger;
import org.safehaus.penrose.partition.PartitionConfig;
import org.safehaus.penrose.connection.ConnectionConfig;
import org.safehaus.penrose.studio.parameter.ParameterDialog;

import javax.naming.InitialContext;
import javax.naming.Context;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Endi S. Dewata
 */
public class NISConnectionPropertiesPage extends FormPage {

    Logger log = Logger.getLogger(getClass());

    FormToolkit toolkit;

    Text nameText;
    Text hostnameText;
    Text domainText;

    Table parametersTable;

    String url;

    NISConnectionEditor editor;
    PartitionConfig partitionConfig;
    ConnectionConfig connectionConfig;

    public NISConnectionPropertiesPage(NISConnectionEditor editor) {
        super(editor, "PROPERTIES", "  Properties  ");

        this.editor = editor;
        this.partitionConfig = editor.getPartitionConfig();
        this.connectionConfig = editor.getConnectionConfig();
    }

    public void createFormContent(IManagedForm managedForm) {
        toolkit = managedForm.getToolkit();

        ScrolledForm form = managedForm.getForm();
        form.setText("Connection Editor");

        Composite body = form.getBody();
        body.setLayout(new GridLayout());

        Section section = toolkit.createSection(body, Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Connection Name");
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Control nameSection = createNameSection(section);
        section.setClient(nameSection);

        section = toolkit.createSection(body, Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Connection Info");
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Control infoSection = createInfoSection(section);
        section.setClient(infoSection);

        section = toolkit.createSection(body, Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Connection Parameters");
        section.setLayoutData(new GridData(GridData.FILL_BOTH));

        Control parametersSection = createParametersSection(section);
        section.setClient(parametersSection);

        refresh();
    }

    public Composite createNameSection(final Composite parent) {

        Composite composite = toolkit.createComposite(parent);
        composite.setLayout(new GridLayout(2, false));

        Label connectionNameLabel = toolkit.createLabel(composite, "Name:");
        GridData gd = new GridData();
        gd.widthHint = 100;
        connectionNameLabel.setLayoutData(gd);

        nameText = toolkit.createText(composite, connectionConfig.getName(), SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        nameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                connectionConfig.setName(nameText.getText());
                checkDirty();
            }
        });

        return composite;
    }

    public Composite createInfoSection(final Composite parent) {

        Composite composite = toolkit.createComposite(parent);
        composite.setLayout(new GridLayout(2, false));

        String url = connectionConfig.getParameter(InitialContext.PROVIDER_URL);
        String hostname = null;
        String domain = null;

        if (url != null) {
            int i = url.indexOf("/", 6);
            hostname = url.substring(6, i);
            domain = url.substring(i+1);
        }

        toolkit.createLabel(composite, "Host:");

        hostnameText = toolkit.createText(composite, "", SWT.BORDER);
        hostnameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (hostname != null) hostnameText.setText(hostname);

        hostnameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                String url = getURL();
                connectionConfig.setParameter(Context.PROVIDER_URL, url);
                checkDirty();
            }
        });

        toolkit.createLabel(composite, "Domain:");

        domainText = toolkit.createText(composite, "", SWT.BORDER);
        domainText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (domain != null) domainText.setText(domain);

        domainText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                String url = getURL();
                connectionConfig.setParameter(Context.PROVIDER_URL, url);
                checkDirty();
            }
        });

        return composite;
    }

    public Composite createParametersSection(final Composite parent) {

        Composite composite = toolkit.createComposite(parent);
        composite.setLayout(new GridLayout(2, false));

        parametersTable = new Table(composite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        parametersTable.setHeaderVisible(true);
        parametersTable.setLinesVisible(true);
        parametersTable.setLayoutData(new GridData(GridData.FILL_BOTH));

        TableColumn tc = new TableColumn(parametersTable, SWT.NONE);
        tc.setText("Name");
        tc.setWidth(250);

        tc = new TableColumn(parametersTable, SWT.NONE);
        tc.setText("Value");
        tc.setWidth(250);

        parametersTable.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent event) {
                try {
                    if (parametersTable.getSelectionCount() == 0) return;

                    int index = parametersTable.getSelectionIndex();
                    TableItem item = parametersTable.getSelection()[0];

                    String oldName = item.getText(0);
                    String oldValue = item.getText(1);

                    ParameterDialog dialog = new ParameterDialog(parent.getShell(), SWT.NONE);
                    dialog.setText("Edit parameter...");
                    dialog.setName(oldName);
                    dialog.setValue(oldValue);
                    dialog.open();

                    if (dialog.getAction() == ParameterDialog.CANCEL) return;

                    String newName = dialog.getName();
                    String newValue = dialog.getValue();

                    if (!oldName.equals(newName)) {
                        connectionConfig.removeParameter(oldName);
                    }

                    connectionConfig.setParameter(newName, newValue);

                    refresh();
                    parametersTable.setSelection(index);
                    checkDirty();

                } catch (Exception e) {
                    log.debug(e.getMessage(), e);
                }
            }
        });

        Composite buttons = toolkit.createComposite(composite, SWT.NONE);
        buttons.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        buttons.setLayout(new GridLayout());

        Button addButton = toolkit.createButton(buttons, "Add", SWT.PUSH);
        addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ParameterDialog dialog = new ParameterDialog(parent.getShell(), SWT.NONE);
                dialog.setText("Add parameter...");
                dialog.open();

                if (dialog.getAction() == ParameterDialog.CANCEL) return;

                connectionConfig.setParameter(dialog.getName(), dialog.getValue());

                refresh();
                checkDirty();
            }
        });

        Button removeButton = toolkit.createButton(buttons, "Delete", SWT.PUSH);
        removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        removeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (parametersTable.getSelectionCount() == 0) return;

                TableItem items[] = parametersTable.getSelection();
                for (int i=0; i<items.length; i++) {
                    String name = items[i].getText(0);
                    connectionConfig.removeParameter(name);
                }

                refresh();
                checkDirty();
            }
        });

        return composite;
    }

    public void refresh() {
        parametersTable.removeAll();

        Collection parameters = connectionConfig.getParameterNames();
        for (Iterator i=parameters.iterator(); i.hasNext(); ) {
            String name = (String)i.next();

            if (Context.PROVIDER_URL.equals(name)) continue;

            String value = connectionConfig.getParameter(name);
            TableItem ti = new TableItem(parametersTable, SWT.NONE);
            ti.setText(0, name);
            ti.setText(1, value);
        }
    }

    public String getURL() {
        String hostname = hostnameText.getText();
        String domain = domainText.getText();

        return "nis://" + hostname + "/" + domain;
    }

    public void checkDirty() {
        editor.checkDirty();
    }
}