package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.repository.CmisRepository;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
                String currentPathId = repository.getFolderIdByRelativePath(pathAcc);
                breadCrumbs.put(bcName, currentPathId);
            }

            // the last item is not part of breadcrumbs
            String lastItem = pathItems[pathItems.length - 1].startsWith("/") ? pathItems[pathItems.length - 1].substring(1) : pathItems[pathItems.length - 1];
            breadCrumbs.remove(lastItem);
            wp.addBreadCrumbs(breadCrumbs);
        }

        // get the links
        ItemIterable<QueryResult> links = repository.getSubFolders(folder);
        for (QueryResult qr : links) {
            String type = qr.getPropertyById("cmis:baseTypeId").getFirstValue().toString();

            if (type.equals("cmis:folder")) {
                String folderId = qr.getPropertyById("cmis:objectId").getFirstValue().toString();
                String folderName = qr.getPropertyById("cmis:name").getFirstValue().toString();
                wp.addLinks(folderName, folderId);
            }
        }

        // get the Contents
        ItemIterable<QueryResult> contents = repository.getSubDocuments(folder, new HashMap<String, String>());
        for (QueryResult qr : contents) {
            CmisObject cmiso = repository.getDocumentById(qr.getPropertyById("cmis:objectId").getFirstValue().toString());
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

        // categories
        ItemIterable<CmisObject> categories = repository.getCategories();
        for (CmisObject cmiso : categories) {
            wp.addCategory(cmiso.getName(), cmiso.getId());
        }

        return wp;
    }


    public WebPage buildSearchResultPage(String siteName, List<String> filters) throws CmisObjectNotFoundException {

        repository.setSiteName(siteName);
        // the homepage has a relative path= ""
        String homePageId = repository.getFolderIdByRelativePath("/");
        WebPage wp = new WebPage("search-result", "Search result", homePageId, false);

        // get the Contents
        ItemIterable<QueryResult> contents = repository.search(homePageId, filters);
        for (QueryResult qr : contents) {
            CmisObject cmiso = repository.getDocumentById(qr.getPropertyById("cmis:objectId").getFirstValue().toString());
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

        return wp;
    }

    public String getPageIdByPath(String path) {
        String folderId = repository.getFolderIdByRelativePath(path);

        return folderId;
    }

    public Downloadable getDownloadable(String id) {
         Document doc = repository.getDocumentById(id);
        return new Downloadable(doc.getName(),doc.getContentStream().getStream(),doc.getContentStreamLength(),doc.getContentStreamMimeType());
    }

}
