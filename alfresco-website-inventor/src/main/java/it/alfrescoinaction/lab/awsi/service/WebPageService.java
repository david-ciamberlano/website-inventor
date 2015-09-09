package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.service.repository.CmisRepository;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebPageService {

    @Autowired
    CmisRepository repository;

    @Value("${alf.homepage}")
    String homePagePath;

    /**
     * Build the domain object representing a webpage
     * @param id the id of the page to build
     * @return the WebPage object
     * @throws CmisObjectNotFoundException
     */
    public WebPage buildWebPage(String id) throws CmisObjectNotFoundException {
        Folder folder = repository.getFolderById(id);

        String folderPath = folder.getPath();
        boolean isHomepage = homePagePath.equals(folderPath);

        WebPage wp = new WebPage(id, folder.getName(), folder.getParentId(), isHomepage);

        String relativeFolderPath = folderPath.replaceFirst(homePagePath+"/","");
        wp.buildBreadCrumbs(relativeFolderPath);

        ItemIterable<CmisObject> children = repository.getChildren(folder);
        for (CmisObject cmiso : children) {
            // path relative to the homepage
            switch(cmiso.getType().getId()) {
                case "cmis:folder": {
                    wp.addLinks(cmiso.getName(), cmiso.getId());
                    break;
                }

                case "cmis:document": {
                    Document doc = (Document)cmiso;
                    switch (doc.getName()) {
                        case ".header.txt": {
                            wp.addSpecialContent("text_header", doc);
                            break;
                        }
                        default:{
                            wp.addContent(doc);
                        }
                    }
                }
            }
        }

        ItemIterable<CmisObject> categories = repository.getCategories();
        for (CmisObject cmiso : categories) {
            wp.addCategory(cmiso.getName(), cmiso.getId());
        }

        return wp;
    }

    public String getPageIdByPath(String path) {
        String folderId = repository.getFolderIdByPath(homePagePath+path);

        return folderId;
    }

    public Downloadable getDownloadable(String id) {
        Document doc = repository.getDocumentById(id);
        return new Downloadable(doc.getName(),doc.getContentStream().getStream(),doc.getContentStreamLength(),doc.getContentStreamMimeType());

    }
}
