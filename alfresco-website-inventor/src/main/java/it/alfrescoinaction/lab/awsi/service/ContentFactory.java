package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.*;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;

import java.io.InputStream;
import java.util.*;

public class ContentFactory {

    public static Content buildContent(Document doc) {

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

                Content textContent = new ContentImpl(doc.getId(), doc.getName(), doc.getContentStreamMimeType(), textType);

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

                Content imageContent = new ContentImpl(doc.getId(),doc.getName(),doc.getContentStreamMimeType(), imgType);

                imageContent.setRenditions(buildRenditions(doc));

                Map<String,String> props = new HashMap<>();

                if (doc.getProperty("exif:pixelXDimension") != null) {
                    props.put("width", doc.getProperty("exif:pixelXDimension").getValueAsString());
                }
                if (doc.getProperty("exif:pixelYDimension") != null) {
                    props.put("height", doc.getProperty("exif:pixelYDimension").getValueAsString());
                }
                long contentSizeInMB = Math.round(doc.getContentStreamLength()/1024);
                props.put("content_size",String.valueOf(contentSizeInMB));

                imageContent.setProperties(props);

                return imageContent;
            }

            default: {
                // caso di file generico
                Content content = new ContentImpl(doc.getId(),doc.getName(),doc.getContentStreamMimeType(), ContentType.GENERIC);

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

        return rends;
    }


}
