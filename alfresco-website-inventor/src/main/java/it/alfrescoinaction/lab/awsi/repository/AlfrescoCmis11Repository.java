package it.alfrescoinaction.lab.awsi.repository;

import it.alfrescoinaction.lab.awsi.exceptions.PageNotFoundException;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;


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
    private String type;


    @Override
    public ItemIterable<CmisObject> getCategories() {
        Session session = connection.getSession();

        CmisObject obj = session.getObjectByPath(alfrescoHomePath);

        // procceed only if the node is a folder
        if (obj.getBaseTypeId().value().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            OperationContext oc = session.createOperationContext();
            oc.setRenditionFilterString("*");
            ItemIterable<CmisObject> children = folder.getChildren(oc);

            return children;
        }
        else {
            throw new PageNotFoundException("Home folder not found: " + alfrescoHomePath);
        }
    }

    @Override
    public Folder getFolderById(String id) throws PageNotFoundException {
        Session session = connection.getSession();

        if (id.equals("home")) {
            OperationContext oc = session.createOperationContext();
            oc.setRenditionFilterString("*");
            id = session.getObjectByPath(alfrescoHomePath, oc).getId();
        }

        CmisObject obj;
        try {
            obj = session.getObject(id);
        }
        catch(CmisObjectNotFoundException e) {
            throw new PageNotFoundException(id);
        }

        // procceed only if the node is a folder
        if (obj.getBaseTypeId().value().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            return folder;
        }
        else {
            throw new PageNotFoundException("Folder not found: "  + id);
        }
    }

    /**
     *
     * @param realtivePath: in the form F1/F2/F3 (no initial /)
     * @return
     * @throws PageNotFoundException
     */
    @Override
    public String getFolderIdByRelativePath(String realtivePath) throws PageNotFoundException {
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
            throw new PageNotFoundException("Folder not found: "  + fullPath);
        }

    }

    @Override
    public Document getDocumentById(String id) throws PageNotFoundException {
        Session session = connection.getSession();

        CmisObject obj;
        try {
            OperationContext oc = session.createOperationContext();
            oc.setRenditionFilterString("*");
            obj = session.getObject(id,oc);
        }
        catch (CmisObjectNotFoundException e){
            throw new PageNotFoundException("Document not found: "  + id);
        }

        if (obj.getBaseTypeId().value().equals("cmis:document") || obj.getBaseTypeId().value().equals("D:cm:thumbnail")){
            Document doc = (Document)obj;
            return doc;
        }
        else {
            throw new PageNotFoundException("Document not found: "  + id);
        }
    }

    @Override
    public ItemIterable<QueryResult> getSubFolders(Folder folder) {
        String queryTemplate = "SELECT F.* FROM cmis:folder F WHERE IN_FOLDER('%s')";
        String query = String.format(queryTemplate,folder.getId());

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<QueryResult> children = session.query(query, false, oc);

        return children;
    }

    @Override
    public ItemIterable<QueryResult> getSubDocuments(Folder folder, Map<String,String> filters) {

        String query = "SELECT D.* FROM cmis:document D WHERE IN_FOLDER('" + folder.getId() + "') ";

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<QueryResult> children = session.query(query, false, oc);

        return children;
    }

    @Override
    public ItemIterable<QueryResult> search(String folderId, List<String> filters) {
        String queryFilters = "";
        String queryFilterTemplateTEXT = "AND %s LIKE '%s' ";
        String queryFilterTemplateDATEFROM = "AND %s >= TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateDATETO = "AND %s <= TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateDATE = "AND %s = TIMESTAMP '%sT00:00:00.000+00:00' ";

        for (int i=0; i<filterNames.length; i++) {
            if (!filters.get(i).isEmpty()) {
                String[] filterParts = filterNames[i].split("\\|");

                String filterId = filterParts[1];
                String filterType = filterParts[2];

                switch (filterType) {
                    case "TEXT": {
                        queryFilters += String.format(queryFilterTemplateTEXT, filterId, filters.get(i));
                        break;
                    }

                    case "%TEXT": {
                        queryFilters += String.format(queryFilterTemplateTEXT, filterId, "%" + filters.get(i));
                        break;
                    }

                    case "TEXT%": {
                        queryFilters += String.format(queryFilterTemplateTEXT, filterId, filters.get(i) + "%");
                        break;
                    }

                    case "%TEXT%": {
                        queryFilters += String.format(queryFilterTemplateTEXT, filterId, "%" + filters.get(i) + "%");
                        break;
                    }

                    case "DATE": {
                        queryFilters += String.format(queryFilterTemplateDATE, filterId, filters.get(i));
                        break;
                    }

                    case "DATE_FROM": {
                        queryFilters += String.format(queryFilterTemplateDATEFROM, filterId, filters.get(i));
                        break;
                    }

                    case "DATE_TO": {
                        queryFilters += String.format(queryFilterTemplateDATETO, filterId, filters.get(i));
                        break;
                    }
                }
            }
        }

        String [] typeParts = type.split("\\|");
        String typeName = typeParts[1];

        String queryTemplate = "SELECT * FROM %s WHERE IN_TREE('workspace://SpacesStore/%s') %s";

        String query = String.format(queryTemplate, typeName, folderId, queryFilters);

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<QueryResult> children = session.query(query, false, oc);

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
