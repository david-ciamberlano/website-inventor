package it.alfrescoinaction.lab.awsi.domain;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;

import java.io.InputStream;

public class ContentFactory {

    public Content buildContent(Document doc) {

        switch (doc.getContentStreamMimeType()) {

            case "text/plain": {
                Content textContent = new ContentImpl(doc.getId(),doc.getName(),doc.getDescription(),doc.getContentStreamMimeType(), ContentType.TEXT);

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
                // caso di file generico
                Content content = new ContentImpl(doc.getId(),doc.getName(),doc.getDescription(),doc.getContentStreamMimeType(),ContentType.IMAGE);
                if (doc.getRenditions().size() > 0) {
                    content.setThumbnailId(doc.getRenditions().get(0).getStreamId());
                }

                return content;
            }


            default: {
                // caso di file generico
                Content content = new ContentImpl(doc.getId(),doc.getName(),doc.getDescription(),doc.getContentStreamMimeType(), ContentType.GENERIC);
                if (doc.getRenditions().size() > 0) {
                    content.setThumbnailId(doc.getRenditions().get(0).getStreamId());
                }

                return content;
            }
        }

    }
}
