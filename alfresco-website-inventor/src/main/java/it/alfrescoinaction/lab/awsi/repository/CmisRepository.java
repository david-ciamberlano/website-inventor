package it.alfrescoinaction.lab.awsi.repository;


import org.apache.chemistry.opencmis.client.api.*;

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

}
