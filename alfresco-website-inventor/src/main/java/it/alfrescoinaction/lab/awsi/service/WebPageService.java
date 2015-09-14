package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.repository.CmisRepository;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WebPageService {

    @Autowired
    CmisRepository repository;

    /**
     * Build the domain object representing a webpage
     * @param id the id of the page to build
     * @return the WebPage object
     * @throws CmisObjectNotFoundException
     */
    public WebPage buildWebPage(String siteName, String id) throws CmisObjectNotFoundException {

        repository.setSiteName(siteName);

        Folder folder = repository.getFolderById(id);
        String folderPath = folder.getPath();

        boolean isHomepage = repository.isHomePage(folderPath);

        WebPage wp = new WebPage(id, folder.getName(), folder.getParentId(), isHomepage);

        if (!isHomepage) {
            // breadcrumbs
            String relativeFolderPath = folderPath.replace(repository.getAlfrescoHomePath(), "");
            String[] pathItems = relativeFolderPath.split("(?=/)");

            String pathAcc = "";
            Map<String, String> breadCrumbs = new LinkedHashMap<>();
            for (String pathItem : pathItems) {
                pathAcc += pathItem;
                String bcName = pathItem.startsWith("/") ? pathItem.substring(1) : pathItem;
                String currentPathId = repository.getFolderIdByPath(pathAcc);
                breadCrumbs.put(bcName, currentPathId);
            }

            // the last item is not part of breadcrumbs
            String lastItem = pathItems[pathItems.length - 1].startsWith("/") ? pathItems[pathItems.length - 1].substring(1) : pathItems[pathItems.length - 1];
            breadCrumbs.remove(lastItem);
            wp.addBreadCrumbs(breadCrumbs);
        }

        // links
        ItemIterable<CmisObject> children = repository.getChildren(folder);
        for (CmisObject cmiso : children) {
            // path relative to the homepage
            switch(cmiso.getBaseTypeId().value()) {
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

        // categories
        ItemIterable<CmisObject> categories = repository.getCategories();
        for (CmisObject cmiso : categories) {
            wp.addCategory(cmiso.getName(), cmiso.getId());
        }

        return wp;
    }

    public String getPageIdByPath(String path) {
        String folderId = repository.getFolderIdByPath(path);

        return folderId;
    }

    public Downloadable getDownloadable(String id) {
        Document doc = repository.getDocumentById(id);
        return new Downloadable(doc.getName(),doc.getContentStream().getStream(),doc.getContentStreamLength(),doc.getContentStreamMimeType());

    }
}
