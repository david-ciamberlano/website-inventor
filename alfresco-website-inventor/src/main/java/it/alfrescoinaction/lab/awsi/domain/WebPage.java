package it.alfrescoinaction.lab.awsi.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * WebPage
 */
public class WebPage {

    private String path;
    private String title;
    private List<Folder> subPages = new ArrayList<>();
    private List<String> content = new ArrayList<>();


    public void addPage (String name, String path) {

        Folder folder = new Folder();

        folder.setPath(path);
        folder.setName(name);

        subPages.add(folder);
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

    public List<Folder> getSubPages() {
        return subPages;
    }

    public void setSubPages(List<Folder> subPages) {
        this.subPages = subPages;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
}
