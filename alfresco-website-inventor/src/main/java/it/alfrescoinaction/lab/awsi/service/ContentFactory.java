package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.*;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ContentFactory {

    public Content buildContent(Document doc) {

        switch (doc.getContentStreamMimeType()) {

            case "text/plain": {
                ContentType textType;

                switch (doc.getName()) {

                    case ".header.txt": {
                        textType = ContentType.TEXT_HEADER;
                        break;
                    }

                    default:
                        textType = ContentType.TEXT;
                }

                Content textContent = new ContentImpl(doc.getId(), doc.getName(), doc.getContentStreamMimeType(), textType);
                Map<String,String> props = new HashMap<>();

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
                Content imageContent = new ContentImpl(doc.getId(),doc.getName(),doc.getContentStreamMimeType(),ContentType.IMAGE);
                if (doc.getRenditions().size() > 0) {
                    imageContent.setThumbnailId(doc.getRenditions().get(0).getStreamId());
                }

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
                if (doc.getRenditions().size() > 0) {
                    content.setThumbnailId(doc.getRenditions().get(0).getStreamId());
                }

                return content;
            }
        }

    }
}
