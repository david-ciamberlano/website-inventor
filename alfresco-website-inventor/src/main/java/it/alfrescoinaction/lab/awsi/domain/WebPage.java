package it.alfrescoinaction.lab.awsi.domain;


import java.util.*;

/**
 * WebPage
 */
public class WebPage {

    private final String id;
    private final String title;
    private final String parentId;
    private final String siteName;
    private final String siteTitle;
    private final String siteDescription;
    private final boolean homepage;

    private SiteProperties siteProperties = null;
    private List<Link> categories = Collections.emptyList();
    private List<Link> links = Collections.emptyList();
    private Map<String,String> breadcrumbs = Collections.emptyMap();
    private List<Content> contents = Collections.emptyList();
    private Map<String,Content> specialContents = Collections.emptyMap();


    public WebPage(String id, String title, String parentId, boolean homepage,
                   String siteName, String siteTitle, String siteDescription) {
        this.id = id;
        this.title = title;
        this.parentId = parentId;
        this.homepage = homepage;
        this.siteName = siteName;
        this.siteTitle = siteTitle;
        this.siteDescription = siteDescription;
    }



    //===== getters/setters =====


    public SiteProperties getSiteProperties() {
        return siteProperties;
    }

    public void setSiteProperties(SiteProperties siteProperties) {
        this.siteProperties = siteProperties;
    }

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

    public void setLinks(List<Link> links) {
        this.links = Collections.unmodifiableList(links);
    }

    public boolean isHomepage() {
        return homepage;
    }

    public List<Content> getContents() {
        // order by priority
        List<Content> contentList = new ArrayList<>(contents);
        contentList.sort(Comparator.comparingInt(Content::getPriority));

        return contentList;
    }


    public void setContents(List<Content> contents) {
        this.contents = Collections.unmodifiableList(contents);
    }

    public List<Link> getCategories(){
        return categories;
    }

    public void setCategories(List<Link> categories) {
        this.categories = Collections.unmodifiableList(categories);
    }

    public Map<String,Content> getSpecialContents() {
        return this.specialContents;
    }

    public void setSpecialContents(Map<String,Content> specialContents) {
        this.specialContents = Collections.unmodifiableMap(specialContents);
    }

    public Map<String,String> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(Map<String,String> breadcrumbs) {
        this.breadcrumbs = Collections.unmodifiableMap(breadcrumbs);
    }

    public String getSiteName() {
        return siteName;
    }

    public String getSiteTitle() { return siteTitle;}

    public String getSiteDescription() {
        return siteDescription;
    }
}
