package it.alfrescoinaction.lab.awsi.repository;

import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.RenditionDownloadable;
import it.alfrescoinaction.lab.awsi.domain.SearchFilterItem;
import it.alfrescoinaction.lab.awsi.domain.SearchFilters;
import it.alfrescoinaction.lab.awsi.exceptions.ObjectNotFoundException;
import it.alfrescoinaction.lab.awsi.exceptions.PageNotFoundException;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
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
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;


@Repository
public class AlfrescoCmisRepository implements CmisRepository {

    @Autowired
    private RemoteConnection connection;

    @Value("${alfresco.serverProtocol}") private String alfrescoServerProtocol;
    @Value("${alfresco.serverUrl}") private String alfrescoServer;
    @Value("${alfresco.serviceEntryPoint}") private String alfrescoServiceEntryPoint;
    @Value("${alfresco.doclibBasePath}") private String alfrescoHomePathTemplate;
    @Value("${alfresco.search.type}") private String searchType;
    @Value("${alfresco.username}") private String username;
    @Value("${alfresco.password}") private String password;


    private String alfrescoHomePath;

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
    public ItemIterable<QueryResult> search(String folderId, SearchFilters filters) {
        String queryFilters = "";
        String queryFilterTemplateTEXT = "AND %s LIKE '%s' ";
        String queryFilterTemplateDATEFROM = "AND %s >= TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateDATETO = "AND %s <= TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateDATE = "AND %s = TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateNUM = "AND %s =  ";
        String queryFilterTemplateNUM_MIN = "AND %s >=  ";
        String queryFilterTemplateNUM_MAX = "AND %s <=  ";

        List <SearchFilterItem> filterItems = filters.getAsList();

        for (SearchFilterItem filter : filterItems) {

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
                    queryFilters += String.format(queryFilterTemplateDATE, filter.getId(), filter.getContent());
                    break;
                }

                case "DATE_FROM": {
                    queryFilters += String.format(queryFilterTemplateDATEFROM, filter.getId(), filter.getContent());
                    break;
                }

                case "DATE_TO": {
                    queryFilters += String.format(queryFilterTemplateDATETO, filter.getId(), filter.getContent());
                    break;
                }
            }
        }

        String [] typeParts = searchType.split("\\|");
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



    public Downloadable getRendition(String type, String objectId, String name) throws ObjectNotFoundException{

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
                if (entity.getContentLength() < 1024*1024) {
                    byte[] buffer2 = EntityUtils.toByteArray(entity);
//                    byte[] buffer = new byte[((Long)entity.getContentLength()).intValue()];
//                    entity.getContent().read(buffer);

                    String mimetype = entity.getContentType().getValue();

                    rend = new RenditionDownloadable(name,buffer2,entity.getContentLength(),mimetype);
                    return rend;
                }
                else {
                    httpget.abort();
                    throw new Exception("Content too large");
                }

            }



        }
        catch (Exception e) {
            //TODO manage exception
            throw new ObjectNotFoundException(e.getMessage());
        }



    }



    //-------------------------- GETTERS/SETTERS --------------------------

    public String getAlfrescoHomePath() {
        return alfrescoHomePath;
    }


}
