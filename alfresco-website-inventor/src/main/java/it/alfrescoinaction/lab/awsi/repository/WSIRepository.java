package it.alfrescoinaction.lab.awsi.repository;

import it.alfrescoinaction.lab.awsi.domain.Content;
import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.Link;
import it.alfrescoinaction.lab.awsi.domain.SiteProperties;
import it.alfrescoinaction.lab.awsi.domain.ecm.WSIFolder;
import it.alfrescoinaction.lab.awsi.exceptions.ObjectNotFoundException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public interface WSIRepository {

    WSIFolder getFolderById(String id) throws NoSuchElementException;

    String getFolderIdByRelativePath(String path) throws NoSuchElementException;

    List<Link> getChildrenFolders(WSIFolder folder);


    Map<String,Content> getChildrenDocuments(WSIFolder folder);
//
//    Document getDocumentById(String id) throws NoSuchElementException;
//
    List<Link> getCategories();

    Downloadable<InputStream> getDownloadable(String id);

    boolean isHomePage(String path);

    String getAlfrescoDocLibPath();

    void init(String siteId);

    String getSiteName();

    String getSiteTitle();

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
    Downloadable<byte[]> getRendition(String type, String objectId);

}
