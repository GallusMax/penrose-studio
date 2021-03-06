package org.safehaus.penrose.studio.federation.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.apache.log4j.Logger;
import org.safehaus.penrose.partition.PartitionClient;
import org.safehaus.penrose.ldap.DN;
import org.safehaus.penrose.source.SourceClient;
import org.safehaus.penrose.studio.dialog.ErrorDialog;

/**
 * @author Endi Sukma Dewata
 */
public class BrowserWizard extends Wizard {

    Logger log = Logger.getLogger(getClass());

    private DN baseDn;
    private PartitionClient partitionClient;
    private SourceClient sourceClient;

    BrowserPage browserPage;

    private String dn;

    public BrowserWizard() {
        setWindowTitle("Browser Wizard");
    }

    public void addPages() {
        browserPage = new BrowserPage();
        browserPage.setBaseDn(baseDn);
        //browserPage.setPartitionClient(partitionClient);
        browserPage.setSourceClient(sourceClient);
        browserPage.setDn(dn);
        addPage(browserPage);
    }

    public boolean performFinish() {
        try {
            dn = browserPage.getDn();
            return true;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ErrorDialog.open(e);
            return false;
        }
    }

    public DN getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(DN baseDn) {
        this.baseDn = baseDn;
    }

    public PartitionClient getPartitionClient() {
        return partitionClient;
    }

    public void setPartitionClient(PartitionClient partitionClient) {
        this.partitionClient = partitionClient;
    }

    public SourceClient getSourceClient() {
        return sourceClient;
    }

    public void setSourceClient(SourceClient sourceClient) {
        this.sourceClient = sourceClient;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }
}
