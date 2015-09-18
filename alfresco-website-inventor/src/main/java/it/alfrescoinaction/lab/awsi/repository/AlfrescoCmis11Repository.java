package it.alfrescoinaction.lab.awsi.repository;

import it.alfrescoinaction.lab.awsi.service.RemoteConnection;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.text.Format;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


@Repository
public class AlfrescoCmis11Repository implements CmisRepository {

    @Autowired
    private RemoteConnection connection;

    @Value("${alf.basePath}")
    private String alfrescoHomePathTemplate;
    private String alfrescoHomePath;

    @Value("${alf.search.filters}")
    private String[] filterNames;

    @Value("${alf.search.type}")
    private String typeName;


    @Override
    public ItemIterable<CmisObject> getCategories() {
        Session session = connection.getSession();

        CmisObject obj = session.getObjectByPath(alfrescoHomePath);

        // procceed only if the node is a folder
        if (obj.getBaseTypeId().value().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            ItemIterable<CmisObject> children = folder.getChildren();

            return children;
        }
        else {
            throw new NoSuchElementException("Home folder not found: " + alfrescoHomePath);
        }
    }

    @Override
    public Folder getFolderById(String id) throws NoSuchElementException {
        Session session = connection.getSession();

        if (id.equals("home")) {
            id = session.getObjectByPath(alfrescoHomePath).getId();
        }

        CmisObject obj = session.getObject(id);

        // procceed only if the node is a folder
        if (obj.getBaseTypeId().value().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            return folder;
        }
        else {
            throw new NoSuchElementException("Folder not found: "  + id);
        }
    }

    /**
     *
     * @param realtivePath: in the form F1/F2/F3 (no initial /)
     * @return
     * @throws NoSuchElementException
     */
    @Override
    public String getFolderIdByRelativePath(String realtivePath) throws NoSuchElementException {
        Session session = connection.getSession();

        // delete initial /
        String fullPath = alfrescoHomePath;
        if (realtivePath.startsWith("/")) {
            fullPath += realtivePath;
        }
        else {
            fullPath += "/" + realtivePath;
        }

        CmisObject obj = session.getObjectByPath(fullPath);

        // procceed only if the node is a folder
        if (obj.getBaseTypeId().value().equals("cmis:folder")){
            return obj.getId();
        }
        else {
            throw new NoSuchElementException("Folder not found: "  + fullPath);
        }

    }

    @Override
    public Document getDocumentById(String id) throws NoSuchElementException {
        Session session = connection.getSession();

        CmisObject obj;
        try {
             obj = session.getObject(id);
        }
        catch (CmisObjectNotFoundException e){
            throw new NoSuchElementException("Document not found: "  + id);
        }

        if (obj.getBaseTypeId().value().equals("cmis:document") || obj.getBaseTypeId().value().equals("D:cm:thumbnail")){
            Document doc = (Document)obj;
            return doc;
        }
        else {
            throw new NoSuchElementException("Document not found: "  + id);
        }
    }

    @Override
    public ItemIterable<QueryResult> getSubFolders(Folder folder) {
        String queryTemplate = "SELECT F.* FROM cmis:folder F WHERE IN_FOLDER('%s')";
        String query = String.format(queryTemplate,folder.getId());

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<QueryResult> children = session.query(query, false);

        return children;
    }

    @Override
    public ItemIterable<QueryResult> getSubDocuments(Folder folder, Map<String,String> filters) {
        String queryFilterTemplate = "AND %s LIKE %%s% ";
        String queryFilter = "";
        for (String filter : filters.keySet()){
            queryFilter += String.format(queryFilterTemplate,filter,filters.get(filter));
        }
        String query = "SELECT D.* FROM cmis:document D WHERE IN_FOLDER('" + folder.getId() + "') " + queryFilter;

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<QueryResult> children = session.query(query, false);

        return children;
    }

    @Override
    public ItemIterable<QueryResult> search(String folderId, List<String> filters) {
        String queryFilterTemplate = "AND %s LIKE '%%%s%%' ";
        String queryFilter = "";

        for (int i=0; i<filterNames.length; i++) {
            if (!filters.get(i).isEmpty()) {
                queryFilter += String.format(queryFilterTemplate,filterNames[i],filters.get(i));
            }
        }

        String query = "SELECT * FROM " + typeName + " WHERE IN_TREE('workspace://SpacesStore/" + folderId + "') " + queryFilter;

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<QueryResult> children = session.query(query, false);

        return children;
    }

    @Override
    public boolean isHomePage(String path) {
        // the final / in folderPath is necessary to match basepath
        return alfrescoHomePath.equals(path);
    }

    @Override
    public void setSiteName(String siteName) {
        this.alfrescoHomePath = MessageFormat.format(alfrescoHomePathTemplate,siteName);
    }



    //-------------------------- GETTERS/SETTERS --------------------------

    public String getAlfrescoHomePath() {
        return alfrescoHomePath;
    }


}
