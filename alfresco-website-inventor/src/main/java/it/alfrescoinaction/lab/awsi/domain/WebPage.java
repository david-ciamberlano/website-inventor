package it.alfrescoinaction.lab.awsi.domain;

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

    public void addContent (String name, String id) {
        AlfContent alfContent = new AlfContent();

        alfContent.setTitle(name);

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
