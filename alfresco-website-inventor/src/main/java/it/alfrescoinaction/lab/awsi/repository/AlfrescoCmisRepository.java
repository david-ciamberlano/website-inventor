package it.alfrescoinaction.lab.awsi.repository;

import com.google.gson.Gson;
import it.alfrescoinaction.lab.awsi.domain.*;
import it.alfrescoinaction.lab.awsi.exceptions.ObjectNotFoundException;
import it.alfrescoinaction.lab.awsi.exceptions.PageNotFoundException;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.util.EmptyItemIterable;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
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

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class AlfrescoCmisRepository implements CmisRepository {

    @Autowired private RemoteConnection connection;

    @Value("${alfresco.serverProtocol}") private String alfrescoServerProtocol;
    @Value("${alfresco.serverUrl}") private String alfrescoServer;
    @Value("${alfresco.serviceEntryPoint}") private String alfrescoServiceEntryPoint;
    @Value("${alfresco.username}") private String username;
    @Value("${alfresco.password}") private String password;

    private String searchType;

    private String alfrescoDocLibPath;
    private String alfrescoSitePath;

    private String siteName;
    private String siteDescription;


    @Override
    public void init(String siteId) {

        // get site & doclib folders
        Session session = connection.getSession();

        String siteFolderquery = "select * from cmis:folder where contains('PATH:\"/app:company_home/st:sites/cm:@@siteid\"')"
                .replace("@@siteid",siteId);

        ItemIterable<QueryResult> siteFolders = session.query(siteFolderquery, false);
        if (siteFolders.getTotalNumItems() != 1) {
            throw new ObjectNotFoundException("Site not found");
        }

        QueryResult siteObj = siteFolders.iterator().next();

        this.alfrescoSitePath = siteObj.getPropertyById("cmis:path").getFirstValue().toString();
        this.alfrescoDocLibPath = alfrescoSitePath + "/documentLibrary";
        this.siteName = siteObj.getPropertyById("cmis:name").getFirstValue().toString();
        this.siteDescription = siteObj.getPropertyById("cmis:description").getFirstValue().toString();

//        this.getSiteProperties();
    }


    @Override
    public List<Folder> getCategories() {
        Session session = connection.getSession();
        Folder alfrescoDocLibFolder = (Folder)session.getObjectByPath(alfrescoDocLibPath);
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<CmisObject> children = alfrescoDocLibFolder.getChildren(oc);

        List<Folder> categories = new ArrayList<>();
        children.forEach(o -> {
            if ("cmis:folder".equals(o.getBaseTypeId().value())){
                categories.add((Folder)o);
            }
        });

        return categories;
    }


    @Override
    public Folder getFolderById(String id) throws PageNotFoundException {
        Session session = connection.getSession();

        if (id.equals("home")) {
           id = session.getObjectByPath(this.alfrescoDocLibPath).getId();
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
    public ItemIterable<QueryResult> getChildrenFolders(Folder folder) {
        String queryTemplate = "SELECT F.* FROM cmis:folder F WHERE IN_FOLDER('%s')";
        String query = String.format(queryTemplate,folder.getId());

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<QueryResult> children = session.query(query, false, oc);

        return children;
    }

    @Override
    public ItemIterable<QueryResult> getChildrenDocuments(Folder folder, Map<String, String> filters) {

        String query = "SELECT D.* FROM cmis:document D WHERE IN_FOLDER('" + folder.getId() + "') ORDER BY D.cmis:name ";

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<QueryResult> children = session.query(query, false, oc);

        return children;
    }

    @Override
    public ItemIterable<QueryResult> search(String folderId, SearchFilters filters) {

        boolean withScore = false;

        String queryBaseTemplate = "SELECT D.* FROM %s WHERE IN_TREE(D,'workspace://SpacesStore/%s') %s AND D.cmis:name <> '.*'";
        String queryWithScoreTemplate = "SELECT D.*, SCORE() rank FROM %s D WHERE IN_TREE(D,'workspace://SpacesStore/%s') %s AND D.cmis:name <> '.*' ORDER BY rank DESC";

        String queryFilters = "";
        String queryFilterTemplateTEXT = "AND D.%s LIKE '%s' ";
        String queryFilterTemplateDATEFROM = "AND D.%s >= TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateDATETO = "AND D.%s <= TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateDATE = "AND D.%s = TIMESTAMP '%sT00:00:00.000+00:00' ";
        String queryFilterTemplateNUM = "AND D.%s = %d";
        String queryFilterTemplateNUM_MIN = "AND D.%s >=  %d";
        String queryFilterTemplateNUM_MAX = "AND D.%s <=  %d";
        String queryFilterTemplateFULLTEXT = "AND CONTAINS(D,'%s')";
        String queryFilterTemplateFULLTEXTEXACT = "AND CONTAINS(D,'\\'%s\\'')";

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

                    // FULLTEXT must be always the last item
                    case "FULLTEXT": {
                        queryFilters += String.format(queryFilterTemplateFULLTEXT, filter.getContent());
                        withScore = true;
                        break;
                    }

                    case "FULLTEXT_E": {
                        queryFilters += String.format(queryFilterTemplateFULLTEXTEXACT, filter.getContent());
                        break;
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

        String queryTemplate;
        if(withScore) {
            queryTemplate = queryWithScoreTemplate;
        }
        else {
            queryTemplate = queryBaseTemplate;
        }

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


    public SiteProperties getSiteProperties() throws ObjectNotFoundException {
        String propertiesFilePath = alfrescoSitePath + "/awsi.config";

        Session session = connection.getSession();

        CmisObject obj = session.getObjectByPath(propertiesFilePath);
        Document awsiConfig;
        // procceed only if the node is a document
        if (obj.getBaseTypeId().value().equals("cmis:document")){
            awsiConfig = (Document)obj;
        }
        else {
            throw new ObjectNotFoundException("Configuration file not found: "  + propertiesFilePath);
        }

        InputStream is = awsiConfig.getContentStream().getStream();

        StringBuilder jsonConf = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonConf.append(line);
            }
        }
        catch (IOException e){
            throw new ObjectNotFoundException("Cannot read configuration");
        }

        Gson gson = new Gson();

        return gson.fromJson(jsonConf.toString(), SiteProperties.class);

    }


//    public Downloadable<byte[]> getRendition(String type, String objectId, String name) throws ObjectNotFoundException {
//
//        List<Rendition> renditions = getDocumentById(objectId).getRenditions();
//
//        renditions.get()
//
//
//        return new RenditionDownloadable (name, buffer, entity.getContentLength(), mimetype);
//
//    }

    public Downloadable<byte[]> getRendition(String type, String objectId, String name) throws ObjectNotFoundException {

        // I'm not using cmis because it doesn't trigger the thumbnail generetion process
        // The rest service generate the thumbnail or eventually return the default placeholder
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

                Downloadable<byte[]> rend;
                //TODO replace magic number
                if (entity.getContentLength() < 1024*1024 || entity.getContentLength() > 0) {
                    byte[] buffer = EntityUtils.toByteArray(entity);
//                    byte[] buffer = new byte[((Long)entity.getContentLength()).intValue()];
//                    entity.getContent().read(buffer);

                    String mimetype = entity.getContentType().getValue();

                    rend = new RenditionDownloadable (name, buffer, entity.getContentLength(), mimetype);
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

    public String getSiteName() {
        return siteName;
    }

    public String getSiteDescription() {
        return siteDescription;
    }

    //-------------------------- PRIVATE --------------------------

    private String getFormattedDate(String date, String type) {
        String formattedDate = "";

        if (date.length() == 4) {
            // there's only the year
            if ("DATE_TO".equals(type)){
                formattedDate = date + "-12-31";
            }
            else {
                formattedDate = date + "-01-01";
            }

        }
        else if (isValid(date,"DATE")) {
            String [] datePart = date.split("\\-");
            if (datePart.length == 3) {
                formattedDate = datePart[2]+"-"+datePart[1]+"-"+datePart[0];
            }
        }

        return formattedDate;
    }


    private boolean isValid(String input, String type) {

        boolean isValid = false;
        Pattern p;

        switch (type) {
            case "DATE": {
                p = Pattern.compile("^(\\d?\\d-\\d?\\d-[1-2]\\d{3})$|^([1-2]\\d{3})$");
                Matcher m = p.matcher(input);
                isValid = m.matches();
                break;
            }

            case "TEXT": {
                p = Pattern.compile("^[a-zA-Z0-9'.,]+$");
                Matcher m = p.matcher(input);
                isValid = m.matches();
                break;
            }
        }


        return isValid;
    }




}
