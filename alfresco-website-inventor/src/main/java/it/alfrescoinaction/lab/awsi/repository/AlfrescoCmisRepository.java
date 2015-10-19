package it.alfrescoinaction.lab.awsi.repository;

import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.RenditionDownloadable;
import it.alfrescoinaction.lab.awsi.domain.SearchFilterItem;
import it.alfrescoinaction.lab.awsi.domain.SearchFilters;
import it.alfrescoinaction.lab.awsi.exceptions.InvalidParameterException;
import it.alfrescoinaction.lab.awsi.exceptions.ObjectNotFoundException;
import it.alfrescoinaction.lab.awsi.exceptions.PageNotFoundException;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.util.EmptyItemIterable;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisException;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@Repository
public class AlfrescoCmisRepository implements CmisRepository {

    @Autowired
    private RemoteConnection connection;

    @Value("${alfresco.serverProtocol}") private String alfrescoServerProtocol;
    @Value("${alfresco.serverUrl}") private String alfrescoServer;
    @Value("${alfresco.serviceEntryPoint}") private String alfrescoServiceEntryPoint;
    @Value("${alfresco.sites}") private String alfrescoSites;
    @Value("${alfresco.doclib}") private String alfrescoDocumentLibrary;
    @Value("${alfresco.search.type}") private String searchType;
    @Value("${alfresco.username}") private String username;
    @Value("${alfresco.password}") private String password;

    private String siteId;
    private String alfrescoDocLibPath;

    @Override
    public ItemIterable<CmisObject> getCategories() {
        Session session = connection.getSession();

        CmisObject obj = session.getObjectByPath(alfrescoDocLibPath);

        // procceed only if the node is a folder
        if (obj.getBaseTypeId().value().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            OperationContext oc = session.createOperationContext();
            oc.setRenditionFilterString("*");
            ItemIterable<CmisObject> children = folder.getChildren(oc);

            return children;
        }
        else {
            throw new PageNotFoundException("Home folder not found: " + alfrescoDocLibPath);
        }
    }

    @Override
    public Folder getFolderById(String id) throws PageNotFoundException {
        Session session = connection.getSession();

        if (id.equals("home")) {
            OperationContext oc = session.createOperationContext();
            oc.setRenditionFilterString("*");
            id = session.getObjectByPath(alfrescoDocLibPath, oc).getId();
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
        String fullPath = alfrescoDocLibPath;
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
    public ItemIterable<QueryResult> search(String folderId, SearchFilters filters) {
        String queryFilters = "";
        String queryFilterTemplateTEXT = "AND %s LIKE '%s' ";
        String queryFilterTemplateDATEFROM = "AND %s >= TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateDATETO = "AND %s <= TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateDATE = "AND %s = TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateNUM = "AND %s = %d";
        String queryFilterTemplateNUM_MIN = "AND %s >=  %d";
        String queryFilterTemplateNUM_MAX = "AND %s <=  %d";
        String queryFilterTemplateFULLTEXT = "AND CONTAINS('\\'%s\\'')";

        List <SearchFilterItem> filterItems = filters.getFilterItems();

        for (SearchFilterItem filter : filterItems) {

            if (!filter.getContent().isEmpty()) {
                switch (filter.getType()) {
                    case "TEXT": {
                        queryFilters += String.format(queryFilterTemplateTEXT, filter.getId(), filter.getContent());
                        break;
                    }

                    case "%TEXT": {
                        queryFilters += String.format(queryFilterTemplateTEXT, filter.getId(), "%" + filter.getContent());
                        break;
                    }

                    case "TEXT%": {
                        queryFilters += String.format(queryFilterTemplateTEXT, filter.getId(), filter.getContent() + "%");
                        break;
                    }

                    case "%TEXT%": {
                        queryFilters += String.format(queryFilterTemplateTEXT, filter.getId(), "%" + filter.getContent() + "%");
                        break;
                    }

                    case "DATE": {
                        String formattedDate = this.getFormattedDate (filter.getContent(), filter.getType());
                        if (!formattedDate.isEmpty()) {
                            queryFilters += String.format(queryFilterTemplateDATE, filter.getId(), formattedDate);
                        }
                        break;
                    }

                    case "DATE_FROM": {
                        String formattedDate = this.getFormattedDate (filter.getContent(), filter.getType());
                        if (!formattedDate.isEmpty()) {
                            queryFilters += String.format(queryFilterTemplateDATEFROM, filter.getId(), formattedDate);
                        }
                        break;
                    }

                    case "DATE_TO": {
                        String formattedDate = this.getFormattedDate (filter.getContent(), filter.getType());
                        if (!formattedDate.isEmpty()) {
                            queryFilters += String.format(queryFilterTemplateDATETO, filter.getId(), formattedDate);
                        }
                        break;
                    }

                    // always the last item
                    case "FULLTEXT": {
                        queryFilters += String.format(queryFilterTemplateFULLTEXT, filter.getContent());
                    }
                }
            }
        }

        // check if at least 1 field is valid (otherwise all the repository will be searched)
        if (queryFilters.isEmpty()) {
            return new EmptyItemIterable<>();
        }

        String [] typeParts = searchType.split("\\|");

        String typeName = typeParts[1] != null?typeParts[1]:"cmis:document";

        String queryTemplate = "SELECT * FROM %s WHERE IN_TREE('workspace://SpacesStore/%s') %s AND cmis:name <> '.*'";

        String query = String.format(queryTemplate, typeName, folderId, queryFilters);

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");

        ItemIterable<QueryResult> children;
        try {
            children = session.query(query, false, oc);
            // if the query is invalid, the following row throws an exception
            children.getTotalNumItems();
        }
        catch (Exception e) {
            children = new EmptyItemIterable<>();
        }

        return children;
    }

    @Override
    public boolean isHomePage(String path) {
        return alfrescoDocLibPath.equals(path);
    }


    @Override
    public Map<String,String> getSiteInfo() {
        Session session = connection.getSession();
        CmisObject cmiso = session.getObjectByPath(alfrescoSites + "/" + siteId);

        Map<String,String> siteInfo = new HashMap<>(2);
        siteInfo.put("name",cmiso.getProperty("cm:title").getValue().toString());
        siteInfo.put("description",cmiso.getProperty("cm:description").getValue().toString());
        return siteInfo;
    }



    public Downloadable getRendition(String type, String objectId, String name) throws ObjectNotFoundException {

        // I'm not using cmis because it doesn't trigger the thumbnail generetion process
        // The rest service generate the thumbnauk or eventually return the default placeholder
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build()) {

            String alfrescoServerUrl = alfrescoServer;
            String requestPath = alfrescoServiceEntryPoint + "/api/node/workspace/SpacesStore/" + objectId +
                    "/content/thumbnails/" + type;

            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(alfrescoServerUrl)
                    .setPath(requestPath)
                    .setParameter("c", "force")
                    .setParameter("ph", "true")
                    .build();

            HttpGet httpget = new HttpGet(uri);

            try (CloseableHttpResponse response = httpclient.execute(httpget)) {

                HttpEntity entity = response.getEntity();

                RenditionDownloadable rend;
                //TODO replace magic number
                if (entity.getContentLength() < 1024*1024 || entity.getContentLength() > 0) {
                    byte[] buffer = EntityUtils.toByteArray(entity);
//                    byte[] buffer = new byte[((Long)entity.getContentLength()).intValue()];
//                    entity.getContent().read(buffer);

                    String mimetype = entity.getContentType().getValue();

                    rend = new RenditionDownloadable(name, buffer, entity.getContentLength(), mimetype);
                    return rend;
                }
                else {
                    httpget.abort();
                    throw new Exception("Content 0 or too large ");
                }
            }
        }
        catch (Exception e) {
            //TODO manage exception
            throw new ObjectNotFoundException(e.getMessage());
        }

    }


    //-------------------------- GETTERS/SETTERS --------------------------

    public String getAlfrescoDocLibPath() {
        return alfrescoDocLibPath;
    }

    @Override
    public void setSite(String siteId) {
        this.siteId = siteId;
        this.alfrescoDocLibPath = "/" + alfrescoSites + "/" + siteId + "/" + alfrescoDocumentLibrary;
    }


    //-------------------------- PRIVATE --------------------------

    private String getFormattedDate(String date, String type) {
        String formattedDate = "";

        if (date.length() == 4) {
            // it's only the year
            if ("DATE_TO".equals(type)){
                formattedDate = date + "-12-31";
            }
            else {
                formattedDate = date + "-01-01";
            }

        }
        else if (date.length() == 10) {
            String [] datePart = date.split("\\-");
            if (datePart.length == 3) {
                formattedDate = datePart[2]+"-"+datePart[1]+"-"+datePart[0];
            }
        }

        return formattedDate;
    }


    private boolean validateInput(String input, String type) {
        switch (type) {
            case "DATE": {
                //^(\d\d?-\d\d?-[1-2]\d{3})$|^([1-2]\d{3})$
                Pattern p = Pattern.compile("");
            }
        }

        return false;
    }
}
