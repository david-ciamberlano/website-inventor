package it.alfrescoinaction.lab.awsi.domain;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;

import java.io.InputStream;
import java.util.List;

public class ContentFactory {

    public Content buildContent(Document doc) {

        switch (doc.getContentStreamMimeType()) {

            case "text/plain": {
                Text textContent = new Text(doc.getId(),doc.getName(),doc.getDescription(),doc.getContentStreamMimeType());

                try (InputStream in =  doc.getContentStream().getStream()) {
                    String text = IOUtils.readAllLines(in);
                    textContent.setText(text);
                }
                catch (Exception e) {
                    //TODO log
                    textContent.setText("");
                }

               return textContent;
            }

            case "image/jpeg":
            case "image/png": {
                Image imageContent = new Image(doc.getId(),doc.getName(),doc.getDescription(),doc.getContentStreamMimeType());

                imageContent.setThumbnailId(doc.getRenditions().get(0).getStreamId());

                return imageContent;
            }


            default: {
                // caso di file generico
                GenericFile genericFile = new GenericFile(doc.getId(),doc.getName(),doc.getDescription(),doc.getContentStreamMimeType());
                return genericFile;
            }
        }

    }
}
