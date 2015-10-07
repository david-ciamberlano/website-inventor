package it.alfrescoinaction.lab.awsi.repository;


import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.RenditionDownloadable;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.http.HttpEntity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public interface CmisRepository {

    Folder getFolderById(String id) throws NoSuchElementException;

    String getFolderIdByRelativePath(String path) throws NoSuchElementException;

    ItemIterable<QueryResult> getSubFolders (Folder folder);

    ItemIterable<QueryResult> getSubDocuments(Folder folder, Map<String,String> filters);

    /**
     * search
     * @param folderId the root folder in which to search
     * @param filters filters to apply to the search
     * @return
     */
    ItemIterable<QueryResult> search(String folderId, List<String> filters);

    Document getDocumentById(String id) throws NoSuchElementException;

    ItemIterable<CmisObject> getCategories();

    boolean isHomePage(String path);

    String getAlfrescoHomePath();

    void setSiteName(String siteName);

    /**
     * Returns the an Inputstream of the selected rendition.
     * If the rendition is not found, returns a standard placeholder
     * http://alfrescolab.it:8080/alfresco/s/api/node/workspace/SpacesStore/{id}/content/thumbnails/{doclib/imagepreview/ecc}?c=force&ph=placeholder
     * @param type
     * @param objectId
     * @return
     */
    Downloadable getRendition(String type, String objectId, String name);

}
