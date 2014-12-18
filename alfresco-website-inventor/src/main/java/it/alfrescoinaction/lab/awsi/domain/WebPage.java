package it.alfrescoinaction.lab.awsi.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * WebPage
 */
public class WebPage {

    private String path;
    private String title;
    private String parentPath;
    private List<Folder> childPages = new ArrayList<>();
    private List<String> content = new ArrayList<>();


    public void addPage (String name, String parentPath) {

        Folder folder = new Folder();

        folder.setPath(path + "/" + name);
        folder.setName(name);

        childPages.add(folder);
    }

    public void addContent (String name, String path) {

    }

    //===== getter/setter =====
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Folder> getChildPages() {
        return childPages;
    }

    public void setChildPages(List<Folder> childPages) {
        this.childPages = childPages;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }
}
