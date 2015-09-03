package it.alfrescoinaction.lab.awsi.domain;

import it.alfrescoinaction.lab.awsi.service.ContentFactory;
import org.apache.chemistry.opencmis.client.api.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebPage
 */
public class WebPage {

    private String id;
    private String title;
    private String parentId;
    private boolean homepage;
    private Map<String,Content> specialContent = new HashMap<>(6);
    private List<Link> categories = new ArrayList<>(10);
    private List<Link> links = new ArrayList<>(10);
    private List<Content> contents = new ArrayList<>(20);
    private ContentFactory contentFactory;

    public WebPage(String id, String title, String parentId, boolean homepage) {
        this.id = id;
        this.title = title;
        this.parentId = parentId;
        this.homepage = homepage;
    }

    public void addLinks(String pageName, String pageId) {
        Link link = new Link();
        link.setId(pageId);
        link.setName(pageName);
        links.add(link);
    }

    public void addContent(Document doc) {
        contentFactory = new ContentFactory();
        contents.add(contentFactory.buildContent(doc));
    }

    public void addCategory(String categoryName, String pageId) {
        Link link = new Link();
        link.setId(pageId);
        link.setName(categoryName);
        categories.add(link);
    }

    public void addSpecialContent(String type, Document doc) {
        contentFactory = new ContentFactory();
        specialContent.put(type, contentFactory.buildContent(doc));
    }

    //===== getters/setters =====

    public String getParentId() {
        return parentId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Link> getLinks() {
        return links;
    }

    public List<Content> getContents() {
        return contents;
    }


    public List<Link> getCategories(){
        return categories;
    }

    public Map<String,Content> getSpecialContent() {
        return this.specialContent;
    }

    public boolean isHomepage() {
        return homepage;
    }

}
