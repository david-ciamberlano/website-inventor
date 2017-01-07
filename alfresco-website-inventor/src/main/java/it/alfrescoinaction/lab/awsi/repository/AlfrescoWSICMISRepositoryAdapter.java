package it.alfrescoinaction.lab.awsi.repository;

import it.alfrescoinaction.lab.awsi.domain.*;
import it.alfrescoinaction.lab.awsi.domain.ecm.WSIFolder;
import it.alfrescoinaction.lab.awsi.exceptions.ObjectNotFoundException;
import org.apache.chemistry.opencmis.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.*;


/**
 * this was written to adapt the old cmis repository to the new (cmis indipendent) wsi interface
 * I'm planning to rewrite the repository with the new rest api (when it will be ready...)
 */
@Repository
public class AlfrescoWSICMISRepositoryAdapter implements WSIRepository {

    private AlfrescoCmisRepository alfrescoCmisRepository;

    @Autowired
    public AlfrescoWSICMISRepositoryAdapter(AlfrescoCmisRepository alfrescoCmisRepository) {
        this.alfrescoCmisRepository = alfrescoCmisRepository;
    }


    @Override
    public WSIFolder getFolderById(String id) throws NoSuchElementException {
        Folder folder = alfrescoCmisRepository.getFolderById(id);
        WSIFolder wsiFolder = new WSIFolder();

        wsiFolder.setId(folder.getId());
        wsiFolder.setName(folder.getName());
        wsiFolder.setPath(folder.getPath());
        wsiFolder.setParentId(folder.getParentId());

        return wsiFolder;
    }

    @Override
    public String getFolderIdByRelativePath(String path) throws NoSuchElementException {
        return alfrescoCmisRepository.getFolderIdByRelativePath(path);
    }

    @Override
    public List<Link> getChildrenFolders(WSIFolder folder) {
        ItemIterable<QueryResult> links = alfrescoCmisRepository.getChildrenFolders(folder.getId());

        List<Link> linkList = new ArrayList<>();
        for (QueryResult qr : links) {
            String type = qr.getPropertyById("cmis:baseTypeId").getFirstValue().toString();

            if ("cmis:folder".equals(type)) {
                String folderId = qr.getPropertyById("cmis:objectId").getFirstValue().toString();
                String folderName = qr.getPropertyById("cmis:name").getFirstValue().toString();
                linkList.add(new Link(folderId, folderName));
            }

        }

        return linkList;
    }

    @Override
    public Map<String,Content> getChildrenDocuments(WSIFolder folder) {
        ItemIterable<QueryResult> pageContents = alfrescoCmisRepository.getChildrenDocuments(folder.getId());
        Map<String,Content> contents = new HashMap<>();

        int index=0;
        for (QueryResult qr : pageContents) {
            index++;
            CmisObject cmiso = alfrescoCmisRepository.getDocumentById(qr.getPropertyById("cmis:objectId").getFirstValue().toString());
            Document doc = (Document)cmiso;
            Optional<Content> content = AlfrescoCmisRepository.buildContent(doc);

            if (content.isPresent()) {
                switch (content.get().getType()) {
                    case TEXT_HEADER: {
                        contents.put("text_header", content.get());
                        break;
                    }
                    case TEXT_FOOTER: {
                        contents.put("text_footer", content.get());
                        break;
                    }
                    default: {
                        contents.put("content_"+index, content.get());
                    }
                }
            }
        }
        return contents;
    }

    @Override
    public List<Link> getCategories() {

        List<Folder> categories = alfrescoCmisRepository.getCategories();

        List<Link> categoryList = new ArrayList<>();
        categories.forEach( f -> categoryList.add(new Link( f.getId(), f.getName())));

        return categoryList;
    }

    public Downloadable<InputStream> getDownloadable(String id) {
        Document doc = alfrescoCmisRepository.getDocumentById(id);
        return new FileDownloadable(doc.getName(),
                doc.getContentStream().getStream(),
                doc.getContentStreamLength(),
                doc.getContentStreamMimeType());

    }

    @Override
    public boolean isHomePage(String path) {
        return alfrescoCmisRepository.isHomePage(path);
    }

    @Override
    public String getAlfrescoDocLibPath() {
        return alfrescoCmisRepository.getAlfrescoDocLibPath();
    }

    @Override
    public void init(String siteId) {
        alfrescoCmisRepository.init(siteId);
    }

    @Override
    public String getSiteName() {
        return alfrescoCmisRepository.getSiteName();
    }

    @Override
    public String getSiteTitle() {
        return alfrescoCmisRepository.getSiteTitle();
    }

    @Override
    public String getSiteDescription() {
        return alfrescoCmisRepository.getSiteDescription();
    }

    @Override
    public SiteProperties getSiteProperties() throws ObjectNotFoundException {
        return alfrescoCmisRepository.getSiteProperties();
    }

    @Override
    public Downloadable<byte[]> getRendition(String type, String objectId) {
        Document doc =  alfrescoCmisRepository.getDocumentById(objectId);

        return alfrescoCmisRepository.getRendition(type, objectId, doc.getName());
    }


}


