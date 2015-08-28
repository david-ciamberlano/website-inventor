package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.service.repository.CmisRepository;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

public class Service {

    @Autowired
    CmisRepository repository;

    /**
     * Build the domain object representing a webpage
     * @param id the id of the page to build
     * @return the WebPage object
     * @throws CmisObjectNotFoundException
     */
    public WebPage buildWebPage(String id) throws CmisObjectNotFoundException{

        Folder folder = repository.getFolderById(id);

        WebPage wp = new WebPage();

        wp.setId(id);
        // retrieve the parent id
        wp.setParentId(folder.getParentId());
        wp.setTitle(folder.getName());

        ItemIterable<CmisObject> children = repository.getChildren(folder);
        for (CmisObject cmiso : children) {
            // path relative to the homepage
            if (cmiso.getType().getId().equals("cmis:folder")) {
                wp.addLinks(cmiso.getName(), cmiso.getId());
            }
            else if (cmiso.getType().getId().equals("cmis:document")) {
                Document doc = (Document)cmiso;
                wp.addContent(doc);
            }
        }

        return wp;
    }

    public Downloadable getDownloadable(String id) {

        Document doc = repository.getDocumentById(id);

        return new Downloadable(doc.getContentStream().getStream(),doc.getContentStreamLength(),doc.getContentStreamMimeType());

    }
}
