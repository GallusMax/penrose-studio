package org.safehaus.penrose.studio.federation.linking;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.safehaus.penrose.ldap.SearchResult;
import org.safehaus.penrose.studio.federation.linking.editor.IdentityLinkingPage;
import org.safehaus.penrose.federation.FederationRepositoryConfig;
import org.safehaus.penrose.federation.IdentityLinkingResult;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Endi Sukma Dewata
 */
public class ImportTask implements IRunnableWithProgress {

    IdentityLinkingPage page;
    FederationRepositoryConfig repository;

    private List<IdentityLinkingResult> results;

    public ImportTask(IdentityLinkingPage page, FederationRepositoryConfig repository) {
        this.page = page;
        this.repository = repository;
    }

    public void run(final IProgressMonitor monitor) throws InvocationTargetException {
        try {
            monitor.beginTask("Importing "+repository.getName()+"...", results.size());

            while (!results.isEmpty()) {
                if (monitor.isCanceled()) throw new InterruptedException();

                IdentityLinkingResult data = results.remove(0);
                SearchResult entry = data.getEntry();

                monitor.subTask("Processing "+entry.getDn()+"...");

                SearchResult importedEntry = page.linkingClient.importEntry(entry);

                data.setSearched(false);
                data.addLinkedEntry(importedEntry);
                data.removeMatchedEntries();
                page.loadLocalEntry(data);

                monitor.worked(1);
            }

        } catch (InterruptedException e) {
            // ignore

        } catch (Exception e) {
            throw new InvocationTargetException(e);

        } finally {
            monitor.done();
        }
    }

    public List<IdentityLinkingResult> getResults() {
        return results;
    }

    public void setResults(List<IdentityLinkingResult> results) {
        this.results = results;
    }
}
