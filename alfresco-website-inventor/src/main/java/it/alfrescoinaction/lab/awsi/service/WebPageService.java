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

    private CmisRepository cmisRepository;

    @Autowired
    public WebPageService (CmisRepository cmisRepository) {
        this.cmisRepository = cmisRepository;
    }

    /**
     * Build the domain object representing a webpage
     * @param id the id of the page to build
     * @return the WebPage object
     * @throws CmisObjectNotFoundException
     */
    public WebPage buildWebPage(String siteId, String id) throws CmisObjectNotFoundException {

        cmisRepository.init(siteId);

        Folder folder = cmisRepository.getFolderById(id);
        String folderPath = folder.getPath();
        boolean isHomepage = cmisRepository.isHomePage(folderPath);

        WebPage wp = new WebPage(id, folder.getName(), folder.getParentId(), isHomepage,
                cmisRepository.getSiteName(), cmisRepository.getSiteTitle(), cmisRepository.getSiteDescription());

        wp.setSiteProperties(cmisRepository.getSiteProperties());

        Map<String, String> breadCrumbs = new LinkedHashMap<>();
        if (!isHomepage) {
            // breadcrumbs
            String relativeFolderPath = folderPath.replace(cmisRepository.getAlfrescoDocLibPath() + "/", "");
            String[] pathItems = relativeFolderPath.split("(?=/)");

            StringBuilder pathAcc = new StringBuilder();
            for (String pathItem : pathItems) {
                pathAcc.append(pathItem);
                String bcName = pathItem.startsWith("/") ? pathItem.substring(1) : pathItem;
                String currentPathId = cmisRepository.getFolderIdByRelativePath(pathAcc.toString());
                breadCrumbs.put(bcName, currentPathId);
            }

            // the last item is not part of breadcrumbs
            String lastItem = pathItems[pathItems.length - 1].startsWith("/") ? pathItems[pathItems.length - 1].substring(1) : pathItems[pathItems.length - 1];
            breadCrumbs.remove(lastItem);
        }

        wp.setBreadcrumbs(breadCrumbs);


        // get the links
        ItemIterable<QueryResult> links = cmisRepository.getChildrenFolders(folder);
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
        ItemIterable<QueryResult> pageContents = cmisRepository.getChildrenDocuments(folder, new HashMap<>());

        List<Content> contents = new ArrayList<>(20);
        Map<String,Content> specialContents = new HashMap<>(6);
        for (QueryResult qr : pageContents) {
            CmisObject cmiso = cmisRepository.getDocumentById(qr.getPropertyById("cmis:objectId").getFirstValue().toString());
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
        List<Folder> categories = cmisRepository.getCategories();
        List<Link> categoryList = new LinkedList<>();
        categories.forEach( f -> categoryList.add(new Link( f.getId(), f.getName())));
        wp.setCategories(categoryList);

        return wp;
    }


    public String getPageIdByPath(String path) {
        return cmisRepository.getFolderIdByRelativePath(path);
    }

    public Downloadable<InputStream> getDownloadable(String id) {
         Document doc = cmisRepository.getDocumentById(id);
        return new FileDownloadable(doc.getName(),
                    doc.getContentStream().getStream(),
                    doc.getContentStreamLength(),
                    doc.getContentStreamMimeType());
    }

    public Downloadable<byte[]> getRendition(String type, String objectId) {
        Document doc =  cmisRepository.getDocumentById(objectId);

        return cmisRepository.getRendition(type, objectId, doc.getName());
    }


}
