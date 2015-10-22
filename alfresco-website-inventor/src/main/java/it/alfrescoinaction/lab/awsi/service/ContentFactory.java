package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.*;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentFactory {

    public static Content buildContent(Document doc) {

        int priority = getDocumentPriority(doc);
        String name = getDocumentName(doc);

        switch (doc.getContentStreamMimeType()) {

            case "text/plain": {
                ContentType textType;

                switch (doc.getName()) {

                    case ".header.txt": {
                        textType = ContentType.TEXT_HEADER;
                        break;
                    }

                    case ".footer.txt": {
                        textType = ContentType.TEXT_FOOTER;
                        break;
                    }

                    default:
                        textType = ContentType.TEXT;
                }

                Content textContent = new ContentImpl(doc.getId(), name, doc.getContentStreamMimeType(), textType, priority);

                List<Property<?>> properties = doc.getProperties();

                Map<String,String> props = new HashMap<>();
                for (Property property : properties) {
                    switch (property.getType().value()) {
                        case "datetime": {
                            GregorianCalendar propertyDate = (GregorianCalendar)property.getFirstValue();
                            if (propertyDate != null) {
                                props.put(property.getLocalName(), String.valueOf(propertyDate.getTimeInMillis()));
                            }
                            break;
                        }

                        default:
                            props.put(property.getLocalName(), property.getValueAsString());
                    }
                }

                textContent.setProperties(props);

                try (InputStream in =  doc.getContentStream().getStream()) {
                    String text = IOUtils.readAllLines(in);
                    props.put("text", text);
                }
                catch (Exception e) {
                    //TODO log
                    props.put("text", "");
                }

                textContent.setProperties(props);

                return textContent;
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

                Content imageContent = new ContentImpl(doc.getId(), name,doc.getContentStreamMimeType(), imgType, priority);

                imageContent.setRenditions(buildRenditions(doc));
                List<Property<?>> properties = doc.getProperties();

                Map<String,String> props = new HashMap<>();
                for (Property property : properties) {
                    switch (property.getType().value()) {
                        case "datetime": {
                            GregorianCalendar propertyDate = (GregorianCalendar)property.getFirstValue();
                            if (propertyDate != null) {
                                props.put(property.getLocalName(), String.valueOf(propertyDate.getTimeInMillis()));
                            }
                            break;
                        }

                        default:
                            props.put(property.getLocalName(), property.getValueAsString());
                    }
                }

                long contentSizeInMB = Math.round(doc.getContentStreamLength()/(1024*1024));
                props.put("content_size",String.valueOf(contentSizeInMB));

                imageContent.setProperties(props);

                return imageContent;
            }

            default: {
                // caso di file generico
                Content content = new ContentImpl(doc.getId(), name,doc.getContentStreamMimeType(), ContentType.GENERIC, priority);

                content.setRenditions(buildRenditions(doc));

                // set all the properties as string---
                // in this way I can use them in the view without casting
                List<Property<?>> properties = doc.getProperties();

                Map<String,String> props = new HashMap<>();
                for (Property property : properties) {
                    switch (property.getType().value()) {
                        case "datetime": {
                            GregorianCalendar propertyDate = (GregorianCalendar)property.getFirstValue();
                            if (propertyDate != null) {
                                props.put(property.getLocalName(), String.valueOf(propertyDate.getTimeInMillis()));
                            }
                            break;
                        }

                        default:
                            props.put(property.getLocalName(), property.getValueAsString());
                    }
                }

                content.setProperties(props);

                return content;
            }
        }

    }



    //****************** private ********************

    private static Map<String,String> buildRenditions(Document doc) {
        Map<String,String> rends = new HashMap<>();
        if (doc.getRenditions().size() > 0) {
            for (Rendition rendition : doc.getRenditions()) {
                rends.put(rendition.getTitle(),rendition.getStreamId());
            }
        }
        else {
            //Default icon

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
}
