package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.*;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 class ContentFactory {

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
                    // sanitize html (or other text type)
                    if ("text/html".equals(mimeType)){
                       safeText = Jsoup.clean(text, Whitelist.relaxed());
                    }
                    else if (doc.getName().endsWith(".md")) {
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
                case '<': builder.append("&lt;"); break;
                case '>': builder.append("&gt;"); break;
                case '&': builder.append("&amp;"); break;
                case '"': builder.append("&quot;"); break;
                case '\n': builder.append("<br>"); break;
                case '\t': builder.append("&nbsp;&nbsp;&nbsp;&nbsp;"); break;
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
