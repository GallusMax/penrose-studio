package org.safehaus.penrose.studio.source.editor;

import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.apache.log4j.Logger;
import org.safehaus.penrose.partition.*;
import org.safehaus.penrose.connection.Connection;
import org.safehaus.penrose.connection.ConnectionConfig;
import org.safehaus.penrose.ldap.*;
import org.safehaus.penrose.source.Source;
import org.safehaus.penrose.source.SourceConfig;
import org.safehaus.penrose.source.FieldConfig;
import org.safehaus.penrose.studio.PenroseStudio;
import org.safehaus.penrose.config.PenroseConfig;
import org.safehaus.penrose.naming.PenroseContext;

import java.util.Collection;
import java.util.Iterator;

public class NISSourceBrowsePage extends FormPage {

    Logger log = Logger.getLogger(getClass());

    FormToolkit toolkit;

    Button refreshButton;
    Text maxSizeText;

    Table table;

    NISSourceEditor editor;
    PartitionConfig partitionConfig;
    SourceConfig sourceConfig;

    public NISSourceBrowsePage(NISSourceEditor editor) throws Exception {
        super(editor, "BROWSE", "  Browse  ");

        this.editor = editor;
        this.partitionConfig = editor.partitionConfig;
        this.sourceConfig = editor.sourceConfig;
    }

    public void createFormContent(IManagedForm managedForm) {
        toolkit = managedForm.getToolkit();

        ScrolledForm form = managedForm.getForm();
        form.setText("Source Editor");

        Composite body = form.getBody();
        body.setLayout(new GridLayout());

        Section section = toolkit.createSection(body, Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Rows");
        section.setLayoutData(new GridData(GridData.FILL_BOTH));

        Control fieldsSection = createFieldsSection(section);
        section.setClient(fieldsSection);

        refresh();
    }

    public Composite createFieldsSection(Composite parent) {

        Composite composite = toolkit.createComposite(parent);
        composite.setLayout(new GridLayout());

        Composite buttons = toolkit.createComposite(composite);
        buttons.setLayout(new GridLayout(3, false));
        buttons.setLayoutData(new GridData());

        Label label = toolkit.createLabel(buttons, "Max:");
        label.setLayoutData(new GridData());

        maxSizeText = toolkit.createText(buttons, "100", SWT.BORDER);
        GridData gd = new GridData();
        gd.widthHint = 50;
        maxSizeText.setLayoutData(gd);

        refreshButton = new Button(buttons, SWT.PUSH);
        refreshButton.setText("Refresh");
        gd = new GridData();
        gd.horizontalIndent = 5;
        gd.widthHint = 80;
        refreshButton.setLayoutData(gd);

        refreshButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                refresh();
            }
        });

        table = toolkit.createTable(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        Collection fields = sourceConfig.getFieldConfigs();
        for (Iterator i=fields.iterator(); i.hasNext(); ) {
            FieldConfig fieldDefinition = (FieldConfig)i.next();

            TableColumn tc = new TableColumn(table, SWT.NONE);
            tc.setText(fieldDefinition.getName());
            tc.setWidth(100);
        }

        return composite;
    }

    public void refresh() {

        final Collection fields = sourceConfig.getFieldConfigs();

        table.removeAll();

        SearchRequest sc = new SearchRequest();

        int size = Integer.parseInt(maxSizeText.getText());
        sc.setSizeLimit(size);

        SearchResponse<SearchResult> sr = new SearchResponse<SearchResult>() {
            public void add(SearchResult searchResult) {
                Attributes attributes = searchResult.getAttributes();
                //log.debug(" - "+av);

                TableItem item = new TableItem(table, SWT.NONE);
                int counter = 0;
                for (Iterator i=fields.iterator(); i.hasNext(); counter++) {
                    FieldConfig fieldDefinition = (FieldConfig)i.next();

                    Attribute attribute = attributes.get(fieldDefinition.getName());
                    if (attribute == null) continue;

                    Collection values = attribute.getValues();

                    String value;
                    if (values == null) {
                        value = null;
                    } else if (values.size() > 1) {
                        value = values.toString();
                    } else {
                        value = values.iterator().next().toString();
                    }

                    item.setText(counter, value == null ? "" : value);
                }
            }
        };

        try {
            PenroseStudio penroseStudio = PenroseStudio.getInstance();
            PenroseConfig penroseConfig = penroseStudio.getPenroseConfig();
            PenroseContext penroseContext = penroseStudio.getPenroseContext();

            Partitions partitions = new Partitions();

            PartitionContext partitionContext = new PartitionContext();
            partitionContext.setPenroseConfig(penroseConfig);
            partitionContext.setPenroseContext(penroseContext);

            Partition partition = partitions.init(partitionConfig, partitionContext);

            ConnectionConfig connectionConfig = partitionConfig.getConnectionConfigs().getConnectionConfig(sourceConfig.getConnectionName());
            Connection connection = partition.createConnection(connectionConfig);

            Source source = partition.createSource(sourceConfig, connection);

            source.search(sc, sr);

            connection.stop();
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String message = e.toString();
            if (message.length() > 500) {
                message = message.substring(0, 500) + "...";
            }
            MessageDialog.openError(editor.getSite().getShell(), "Browse Failed", message);
        }
    }

    public void setDirty(boolean dirty) {
        editor.setDirty(dirty);
    }
}
