package it.alfrescoinaction.lab.awsi.repository;


import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;

import java.util.NoSuchElementException;


public interface CmisRepository {

    Folder getFolderById(String id) throws NoSuchElementException;

    String getFolderIdByPath(String path) throws NoSuchElementException;

    ItemIterable<CmisObject> getChildren (Folder folder);

    Document getDocumentById(String id) throws NoSuchElementException;

    ItemIterable<CmisObject> getCategories();

    boolean isHomePage(String path);

    String getAlfrescoHomePath();

    void setSiteName(String siteName);

}
