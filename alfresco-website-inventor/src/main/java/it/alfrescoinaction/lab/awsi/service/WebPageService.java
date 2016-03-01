package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.*;
import it.alfrescoinaction.lab.awsi.repository.CmisRepository;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

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
    public WebPage buildWebPage(String siteId, String id) throws CmisObjectNotFoundException {

        repository.init(siteId);

        Folder folder = repository.getFolderById(id);
        String folderPath = folder.getPath();
        boolean isHomepage = repository.isHomePage(folderPath);

        WebPage wp = new WebPage(id, folder.getName(), folder.getParentId(), isHomepage,
                repository.getSiteName(), repository.getSiteTitle(), repository.getSiteDescription());

        wp.setSiteProperties(repository.getSiteProperties());

        Map<String, String> breadCrumbs = new LinkedHashMap<>();
        if (!isHomepage) {
            // breadcrumbs
            String relativeFolderPath = folderPath.replace(repository.getAlfrescoDocLibPath() + "/", "");
            String[] pathItems = relativeFolderPath.split("(?=/)");

            StringBuilder pathAcc = new StringBuilder();
            for (String pathItem : pathItems) {
                pathAcc.append(pathItem);
                String bcName = pathItem.startsWith("/") ? pathItem.substring(1) : pathItem;
                String currentPathId = repository.getFolderIdByRelativePath(pathAcc.toString());
                breadCrumbs.put(bcName, currentPathId);
            }

            // the last item is not part of breadcrumbs
            String lastItem = pathItems[pathItems.length - 1].startsWith("/") ? pathItems[pathItems.length - 1].substring(1) : pathItems[pathItems.length - 1];
            breadCrumbs.remove(lastItem);
        }

        wp.setBreadcrumbs(breadCrumbs);


        // get the links
        ItemIterable<QueryResult> links = repository.getChildrenFolders(folder);
        List<Link> linkList = new ArrayList<>((int)links.getTotalNumItems());
        for (QueryResult qr : links) {
            String type = qr.getPropertyById("cmis:baseTypeId").getFirstValue().toString();

            if ("cmis:folder".equals(type)) {
                String folderId = qr.getPropertyById("cmis:objectId").getFirstValue().toString();
                String folderName = qr.getPropertyById("cmis:name").getFirstValue().toString();
                linkList.add(new Link(folderId,folderName));
            }
        }
        wp.setLinks(linkList);

        // get the Contents
        ItemIterable<QueryResult> pageContents = repository.getChildrenDocuments(folder, new HashMap<>());

        List<Content> contents = new ArrayList<>(20);
        Map<String,Content> specialContents = new HashMap<>(6);
        for (QueryResult qr : pageContents) {
            CmisObject cmiso = repository.getDocumentById(qr.getPropertyById("cmis:objectId").getFirstValue().toString());
            Document doc = (Document)cmiso;
            Optional<Content> content = ContentFactory.buildContent(doc);

            if (content.isPresent()) {
                switch (content.get().getType()) {
                    case TEXT_HEADER: {
                        specialContents.put("text_header", content.get());
                        break;
                    }
                    case TEXT_FOOTER: {
                        specialContents.put("text_footer", content.get());
                        break;
                    }
                    default: {
                        contents.add(content.get());
                    }
                }
            }
        }
        wp.setContents(contents);
        wp.setSpecialContents(specialContents);

        // categories
        List<Folder> categories = repository.getCategories();
        List<Link> categoryList = new LinkedList<>();
        categories.forEach( f -> categoryList.add(new Link( f.getId(), f.getName())));
        wp.setCategories(categoryList);

        return wp;
    }


    public WebPage buildSearchResultPage(String siteId, SearchFilters filters) throws CmisObjectNotFoundException {

        // the homepage has a relative path = "/"
        String homePageId = repository.getFolderIdByRelativePath("/");
        WebPage wp = new WebPage("search-result", "Search result", homePageId, false, repository.getSiteName(), repository.getSiteTitle(), repository.getSiteDescription());

        wp.setSiteProperties(repository.getSiteProperties());

        // get the Contents
        ItemIterable<QueryResult> searchContents = repository.search(homePageId, filters);
        List<Content> contents = new ArrayList<>();
        for (QueryResult qr : searchContents) {
            CmisObject cmiso = repository.getDocumentById(qr.getPropertyById("cmis:objectId").getFirstValue().toString());
            Document doc = (Document)cmiso;
            Optional<Content> content = ContentFactory.buildContent(doc);
            if (content.isPresent()) {
                contents.add(content.get());
            }
        }
        wp.setContents(contents);

        return wp;
    }

    public String getPageIdByPath(String path) {
        return repository.getFolderIdByRelativePath(path);
    }

    public Downloadable<InputStream> getDownloadable(String id) {
         Document doc = repository.getDocumentById(id);
        return new FileDownloadable(doc.getName(),
                    doc.getContentStream().getStream(),
                    doc.getContentStreamLength(),
                    doc.getContentStreamMimeType());
    }

    public Downloadable<byte[]> getRendition(String type, String objectId) {
        Document doc =  repository.getDocumentById(objectId);

        Downloadable<byte[]> downloadable = repository.getRendition(type, objectId, doc.getName());

        return downloadable;
    }


}
