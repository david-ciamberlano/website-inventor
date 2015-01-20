package it.alfrescoinaction.lab.awsi.domain;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * WebPage
 */
public class WebPage {

    private String id;
    private String title;
    private String parentId;
    private List<AlfFolder> childPages = new ArrayList<>();
    private List<AlfContent> contents = new ArrayList<>();


    public void addPage (String pageName, String pageId) {

        AlfFolder alfFolder = new AlfFolder();

        alfFolder.setId(pageId);
        alfFolder.setName(pageName);

        childPages.add(alfFolder);
    }

    public void addContent (Document doc) {

        AlfContent alfContent = new AlfContent();

        alfContent.setTitle( doc.getName());

        switch (doc.getContentStreamMimeType()) {

            case "text/plain":

                alfContent.setType(AlfContentType.TEXT);

                try (InputStream in =  doc.getContentStream().getStream()) {

                    String text = IOUtils.readAllLines(in);
                    alfContent.setText(text);
                }
                catch (Exception ioe) {
                    //TODO log
                }
                break;

            case "image/jpeg":

                alfContent.setType(AlfContentType.IMAGE);



                break;
        }


        contents.add(alfContent);

    }

    //===== getter/setter =====


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AlfFolder> getChildPages() {
        return childPages;
    }

    public void setChildPages(List<AlfFolder> childPages) {
        this.childPages = childPages;
    }

    public List<AlfContent> getContents() {
        return contents;
    }

    public void setContents(List<AlfContent> contents) {
        this.contents = contents;
    }
}
