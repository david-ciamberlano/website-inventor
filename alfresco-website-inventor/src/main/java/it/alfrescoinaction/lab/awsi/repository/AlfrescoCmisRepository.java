package it.alfrescoinaction.lab.awsi.repository;

import com.google.gson.Gson;
import it.alfrescoinaction.lab.awsi.domain.*;
import it.alfrescoinaction.lab.awsi.exceptions.ObjectNotFoundException;
import it.alfrescoinaction.lab.awsi.exceptions.PageNotFoundException;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Repository
public class AlfrescoCmisRepository {

    private static final Logger logger = LogManager.getLogger(AlfrescoCmisRepository.class);

    private RemoteConnection connection;

    @Autowired
    public AlfrescoCmisRepository(RemoteConnection connection) {
        this.connection = connection;
    }

    @Value("${alfresco.serverProtocol}") private String alfrescoServerProtocol;
    @Value("${alfresco.serverUrl}") private String alfrescoServer;
    @Value("${alfresco.serviceEntryPoint}") private String alfrescoServiceEntryPoint;
    @Value("${alfresco.username}") private String username;
    @Value("${alfresco.password}") private String password;

    private String alfrescoDocLibPath;
    private String alfrescoSitePath;

    private String siteName;
    private String siteDescription;
    private String siteTitle;
    private String siteId;

    private Resource defaultIcon;


    public void init(String siteId) {

        // get site & doclib folders
        Session session = connection.getSession();

        // get the site root objectId (in this way I bypass the problem of the translated site folder name in the cmis path)
        String siteFolderquery = ("select * from cmis:folder F join cm:titled T " +
                "on F.cmis:objectId = T.cmis:objectId  " +
                "where contains(F,'PATH:\"/app:company_home/st:sites/cm:@@siteid\"')")
                .replace("@@siteid",siteId);

        ItemIterable<QueryResult> siteFolders = session.query(siteFolderquery, false);
        if (siteFolders.getTotalNumItems() != 1) {
            throw new ObjectNotFoundException("Site not found");
        }

        QueryResult siteObj = siteFolders.iterator().next();

        alfrescoSitePath = siteObj.getPropertyById("cmis:path").getFirstValue().toString();
        alfrescoDocLibPath = alfrescoSitePath + "/documentLibrary";
        siteName = siteObj.getPropertyById("cmis:name").getFirstValue().toString();
        siteTitle = siteObj.getPropertyById("cm:title").getFirstValue().toString();
        siteDescription = siteObj.getPropertyById("cmis:description").getFirstValue().toString();

//        this.getSiteProperties();
    }


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


    public Folder getFolderById(String id) throws PageNotFoundException {
        Session session = connection.getSession();

        if ("home".equals(id)) {
           id = session.getObjectByPath(alfrescoDocLibPath).getId();
        }

        CmisObject obj;
        try {
            obj = session.getObject(id);
        }
        catch(CmisObjectNotFoundException e) {
//            logger.error("Page not found. " + e.getMessage());
            throw new PageNotFoundException(id);
        }

        // procceed only if the node is a folder
        if ("cmis:folder".equals(obj.getBaseTypeId().value())){
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

        if (obj.getBaseTypeId().value().equals("cmis:document") ||
                obj.getBaseTypeId().value().equals("D:cm:thumbnail")){
            Document doc = (Document)obj;
            return doc;
        }
        else {
            throw new PageNotFoundException("Document not found: "  + id);
        }
    }

    public ItemIterable<QueryResult> getChildrenFolders(String folderId) {
        String queryTemplate = "SELECT F.* FROM cmis:folder F WHERE IN_FOLDER('%s') ORDER BY F.cmis:name";
        String query = String.format(queryTemplate,folderId);

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<QueryResult> children = session.query(query, false, oc);

        return children;
    }

    public ItemIterable<QueryResult> getChildrenDocuments(String folderId) {

        String query = "SELECT D.* FROM cmis:document D WHERE IN_FOLDER('"
                + folderId + "') ORDER BY D.cmis:name ";

        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");

        return session.query(query, false, oc);
    }

    public boolean isHomePage(String path) {
        return alfrescoDocLibPath.equals(path);
    }


    public SiteProperties getSiteProperties() throws ObjectNotFoundException {
        String propertiesFilePath = alfrescoDocLibPath + "/.awsi.config";

        Session session = connection.getSession();

        CmisObject obj = session.getObjectByPath(propertiesFilePath);
        obj.refresh();

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


    public Downloadable<byte[]> getRendition(String type, String objectId, String name) {

        List<Rendition> renditions = getDocumentById(objectId).getRenditions();

        Optional<Rendition> rendition = renditions!=null ?
                renditions.stream().filter(r -> type.equals(r.getTitle())).findFirst() : Optional.empty();

        if (rendition.isPresent()) {
            ContentStream cs = rendition.get().getContentStream();

            byte[] buffer;

            try {
                buffer = toByteArray(cs.getStream());
            }
            catch (IOException e) {
                buffer = new byte[0];
            }

            return  new RenditionDownloadable (name, buffer, cs.getLength(), cs.getMimeType());
        }
        else {
            return getRenditionRest(type,objectId,name);
        }

    }

    private Downloadable<byte[]> getRenditionRest(String type, String objectId, String name)
            throws ObjectNotFoundException {

        // I'm not using cmis because it doesn't trigger the thumbnail generetion process
        // The rest service generate the thumbnail or eventually return the default placeholder
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        try (CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credentialsProvider).build()) {

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
                if (entity.getContentLength() < 1024*1024 && entity.getContentLength() > 0) {
                    byte[] buffer = EntityUtils.toByteArray(entity);
//                    byte[] buffer = new byte[((Long)entity.getContentLength()).intValue()];
//                    entity.getContent().read(buffer);

                    String mimetype = entity.getContentType().getValue();

                    rend = new RenditionDownloadable (name, buffer, buffer.length, mimetype);
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

    public String getSiteTitle() {
        return siteTitle;
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
            String [] datePart = date.split("-");
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

    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
    }


    static Optional<Content> buildContent(Document doc) {

        int priority = getDocumentPriority(doc);
        String name = getDocumentName(doc);
        String title = getDocumentTitle(doc);
        String description = getDocumentDescription(doc);
        String mimeType = doc.getContentStreamMimeType();

        // check if the document is hidden and not a special one
        if (name.startsWith(".") && !name.startsWith(".header") && !name.startsWith(".footer")) {
            return Optional.empty();
        }

        // check if mimetype is not null
        if (mimeType == null || mimeType.isEmpty()) {
            return Optional.empty();
        }

        switch (mimeType) {

            case "text/html":
            case "text/markdown":
            case "text/x-markdown":
            case "text/plain": {
                ContentType textType;

                switch (doc.getName()) {

                    case ".header.txt":
                    case ".header.md":
                    case ".header.html":
                    case ".header": {
                        textType = ContentType.TEXT_HEADER;
                        break;
                    }

                    case ".footer.txt":
                    case ".footer.md":
                    case ".footer.html":
                    case ".footer": {
                        textType = ContentType.TEXT_FOOTER;
                        break;
                    }

                    default:
                        textType = ContentType.TEXT;
                }

                Content textContent = new ContentImpl(doc.getId(), name, title, description,
                        doc.getContentStreamMimeType(), textType, priority);

                Map<String,String> props = extractProperties(doc.getProperties());

                textContent.setProperties(props);

                try (InputStream in = doc.getContentStream().getStream()) {
                    String text = IOUtils.readAllLines(in);

                    String safeText;

                    if ("text/html".equals(mimeType) || doc.getName().endsWith(".html")){
                        // sanitize html
                        safeText = Jsoup.clean(text, Whitelist.relaxed());
                    }
                    else if ("text/markdown".equals(mimeType) || "text/x-markdown".equals(mimeType)
                            || doc.getName().endsWith(".md")) {
                        // convert markdown
                        Parser parser = Parser.builder().build();
                        Node document = parser.parse(text);
                        HtmlRenderer renderer = HtmlRenderer.builder().build();
                        safeText = renderer.render(document);
                    }
                    else {
                        safeText = text2html(text);
                    }

                    props.put("text", safeText);
                } catch (Exception e) {
                    //TODO log
                    props.put("text", "");
                }

                textContent.setProperties(props);

                return Optional.of(textContent);
            }


            case "image/jpeg":
            case "image/png": {

                ContentType imgType;
                switch (doc.getName()) {

                    case ".header.png":
                    case ".header.jpg": {
                        imgType = ContentType.IMAGE_HEADER;
                        break;
                    }

                    case ".footer.png":
                    case ".footer.jpg": {
                        imgType = ContentType.IMAGE_FOOTER;
                        break;
                    }

                    default:
                        imgType = ContentType.IMAGE;
                }

                Content imageContent = new ContentImpl(doc.getId(), name, title, description,
                        doc.getContentStreamMimeType(), imgType, priority);

                imageContent.setRenditions(buildRenditions(doc));

                Map<String,String> props = extractProperties(doc.getProperties());

                long contentSizeInMB = Math.round(doc.getContentStreamLength()/(1024*1024));
                props.put("content_size",String.valueOf(contentSizeInMB));

                imageContent.setProperties(props);

                return Optional.of(imageContent);
            }

            default: {
                // caso di file generico
                Content content = new ContentImpl(doc.getId(), name, title, description,
                        doc.getContentStreamMimeType(), ContentType.GENERIC, priority);

                content.setRenditions(buildRenditions(doc));

                // set all the properties as string---
                // in this way I can use them in the view without casting
                content.setProperties(extractProperties(doc.getProperties()));

                return Optional.of(content);
            }
        }

    }



    //****************** private ********************

    private static Map<String,String> extractProperties(List<Property<?>> properties) {
        Map<String,String> props = new HashMap<>();

        for (Property property : properties) {
            switch (property.getType().value()) {
                case "datetime": {
                    GregorianCalendar propertyDate = (GregorianCalendar)property.getFirstValue();
                    if (propertyDate != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        sdf.setCalendar(propertyDate);
                        props.put(property.getLocalName(), sdf.format(propertyDate.getTime()));
                    }
                    break;
                }

                default:
                    props.put(property.getLocalName(), property.getValueAsString());
            }
        }

        return props;
    }

    private static Map<String,String> buildRenditions(Document doc) {
        Map<String,String> rends = new HashMap<>();
        List<Rendition> renditions = doc.getRenditions();
        if (renditions!=null && renditions.size() > 0) {
            for (Rendition rendition : renditions) {
                rends.put(rendition.getTitle(),rendition.getStreamId());
            }
        }
        else {
            //TODO Default icon

        }

        return rends;
    }


    private static String getDocumentName(Document doc) {

        String name = doc.getName();
        String regex = "^(#\\d{1,3} )?(.*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);

        if (m.find()) {
            name = m.group(2);
        }

        return name;
    }

    private static String getDocumentTitle(Document doc) {
        return doc.getProperty("cm:title")!=null?doc.getProperty("cm:title").getValueAsString():"";
    }

    private static String getDocumentDescription(Document doc) {
        return doc.getProperty("cm:description")!=null?doc.getProperty("cm:description").getValueAsString():"";
    }

    private static int getDocumentPriority(Document doc) {
        int priority = 900;

        String name = doc.getName();
        String regex = "^#(\\d{1,3}) ";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);

        if (m.find()) {
            priority = Integer.parseInt(m.group(1));
        }

        return priority;
    }

    /**
     * convert plain text to html
     * (snippet copied from stackoverflow)
     */
    private static String text2html(String text) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;
        for(char c : text.toCharArray()) {
            if(c == ' ') {
                if( previousWasASpace ) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }

            switch(c) {
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                case '&':
                    builder.append("&amp;");
                    break;
                case '"':
                    builder.append("&quot;");
                    break;
                case '\n':
                    builder.append("<br>");
                    break;
                case '\t':
                    builder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    break;
                default:
                    if( c < 128 ) {
                        builder.append(c);
                    } else {
                        builder.append("&#").append((int)c).append(";");
                    }
            }
        }
        return builder.toString();
    }


}
