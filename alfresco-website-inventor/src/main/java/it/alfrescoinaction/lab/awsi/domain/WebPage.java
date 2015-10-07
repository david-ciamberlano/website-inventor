package it.alfrescoinaction.lab.awsi.domain;

import it.alfrescoinaction.lab.awsi.service.ContentFactory;
import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * WebPage
 */
public class WebPage {

    private final String id;
    private final String title;
    private final String parentId;
    private final boolean homepage;
    private Map<String,Content> specialContent = new HashMap<>(6);
    private List<Link> categories = new ArrayList<>(10);
    private List<Link> links = new ArrayList<>(10);
    private List<Content> contents = new ArrayList<>(20);
    private Map<String,String> breadcrumbs = new LinkedHashMap<>(10);

    public WebPage(String id, String title, String parentId, boolean homepage) {
        this.id = id;
        this.title = title;
        this.parentId = parentId;
        this.homepage = homepage;
    }

    public void addLinks(String pageName, String id) {
        Link link = new Link(id, pageName);
        links.add(link);
    }

    public void addContent(Document doc) {
        contents.add (ContentFactory.buildContent(doc));
    }

    public void addCategory(String categoryName, String id) {
        Link link = new Link(id, categoryName);
        categories.add(link);
    }

    public void addBreadCrumbs(Map<String,String> bc) {
        this.breadcrumbs.putAll(bc);
    }

    public void addSpecialContent(String type, Document doc) {
        specialContent.put(type, ContentFactory.buildContent(doc));
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

    public Map<String,String> getBreadcrumbs() {
        return breadcrumbs;
    }

}
