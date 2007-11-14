package org.safehaus.penrose.studio.federation.editor;

import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.apache.log4j.Logger;
import org.safehaus.penrose.connection.ConnectionConfig;
import org.safehaus.penrose.studio.federation.Federation;
import org.safehaus.penrose.studio.project.Project;
import org.safehaus.penrose.studio.dialog.ErrorDialog;
import org.safehaus.penrose.management.PenroseClient;
import org.safehaus.penrose.management.PartitionClient;
import org.safehaus.penrose.management.ConnectionClient;

/**
 * @author Endi S. Dewata
 */
public class FederationDatabasePage extends FormPage {

    Logger log = Logger.getLogger(getClass());

    FormToolkit toolkit;

    FederationEditor editor;
    Federation federation;

    public FederationDatabasePage(FederationEditor editor, Federation federation) {
        super(editor, "DATABASE", "  Database  ");

        this.editor = editor;
        this.federation = federation;
    }

    public void createFormContent(IManagedForm managedForm) {
        try {
            toolkit = managedForm.getToolkit();

            ScrolledForm form = managedForm.getForm();
            form.setText("Database");

            Composite body = form.getBody();
            body.setLayout(new GridLayout());

            Section databaseSection = toolkit.createSection(body, Section.TITLE_BAR | Section.EXPANDED);
            databaseSection.setText("Database");
            databaseSection.setLayoutData(new GridData(GridData.FILL_BOTH));

            Control databaseControl = createDatabaseControl(databaseSection);
            databaseSection.setClient(databaseControl);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ErrorDialog.open(e);
        }
    }

    public Composite createDatabaseControl(Composite parent) throws Exception {

        Composite composite = toolkit.createComposite(parent);
        composite.setLayout(new GridLayout(2, false));

        Project project = federation.getProject();
        PenroseClient client = project.getClient();
        PartitionClient partitionClient = client.getPartitionClient(Federation.PARTITION);
        ConnectionClient connectionClient = partitionClient.getConnectionClient(Federation.JDBC);
        ConnectionConfig connectionConfig = connectionClient.getConnectionConfig();

        //Partition partition = federation.getPartition();
        //JDBCConnection connection = (JDBCConnection)partition.getConnection(Federation.JDBC);
        //ConnectionConfig connectionConfig = connection.getConnectionConfig();

        Label driverLabel = toolkit.createLabel(composite, "Driver:");
        GridData gd = new GridData();
        gd.widthHint = 100;
        driverLabel.setLayoutData(gd);

        String driver = connectionConfig.getParameter("driver");
        Label driverText = toolkit.createLabel(composite, driver == null ? "" : driver);
        driverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label domainLabel = toolkit.createLabel(composite, "URL:");
        domainLabel.setLayoutData(new GridData());

        String url = connectionConfig.getParameter("url");
        Label domainText = toolkit.createLabel(composite, url == null ? "" : url);
        domainText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label serverLabel = toolkit.createLabel(composite, "Username:");
        serverLabel.setLayoutData(new GridData());

        String user = connectionConfig.getParameter("user");
        Label serverText = toolkit.createLabel(composite, user == null ? "" : user);
        serverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label suffixLabel = toolkit.createLabel(composite, "Password:");
        suffixLabel.setLayoutData(new GridData());

        Label suffixText = toolkit.createLabel(composite, "*****");
        suffixText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return composite;
    }
}
