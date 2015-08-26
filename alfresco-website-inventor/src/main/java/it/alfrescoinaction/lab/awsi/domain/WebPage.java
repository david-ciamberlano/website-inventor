package it.alfrescoinaction.lab.awsi.domain;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * WebPage
 */
public class WebPage {

    private String id;
    private String title;
    private String parentId;
    private List<Link> links = new ArrayList<>();
    private List<Content> contents = new ArrayList<>();


    public void addLinks(String pageName, String pageId) {

        Link link = new Link();

        link.setId(pageId);
        link.setName(pageName);

        links.add(link);
    }

    public void addContent (Document doc) {

        ContentFactory contentFactory = new ContentFactory();
        contents.add(contentFactory.buildContent(doc));

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

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
}
