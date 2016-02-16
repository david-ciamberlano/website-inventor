package it.alfrescoinaction.lab.awsi.repository;


import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.SearchFilters;
import it.alfrescoinaction.lab.awsi.domain.SiteProperties;
import it.alfrescoinaction.lab.awsi.exceptions.ObjectNotFoundException;
import org.apache.chemistry.opencmis.client.api.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public interface CmisRepository {

    Folder getFolderById(String id) throws NoSuchElementException;

    String getFolderIdByRelativePath(String path) throws NoSuchElementException;

    ItemIterable<QueryResult> getChildrenFolders(Folder folder);

    ItemIterable<QueryResult> getChildrenDocuments(Folder folder, Map<String, String> filters);

    /**
     * search
     * @param folderId the root folder in which to search
     * @param filters filters to apply to the search
     * @return
     */
    ItemIterable<QueryResult> search(String folderId, SearchFilters filters);

    Document getDocumentById(String id) throws NoSuchElementException;

    List<Folder> getCategories();

    boolean isHomePage(String path);

    String getAlfrescoDocLibPath();

    void init(String siteId);

    String getSiteName();

    String getSiteDescription();

    SiteProperties getSiteProperties() throws ObjectNotFoundException;

    /**
     * Returns the an Inputstream of the selected rendition.
     * If the rendition is not found, returns a standard placeholder
     * http://alfrescolab.it:8080/alfresco/s/api/node/workspace/SpacesStore/{id}/content/thumbnails/{doclib/imagepreview/ecc}?c=force&ph=placeholder
     * @param type
     * @param objectId
     * @return
     */
    Downloadable<byte[]> getRendition(String type, String objectId, String name) throws ObjectNotFoundException;

}
