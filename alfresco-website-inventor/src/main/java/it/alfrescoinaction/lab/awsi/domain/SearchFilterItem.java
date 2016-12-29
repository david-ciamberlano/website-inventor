package it.alfrescoinaction.lab.awsi.domain;

public class SearchFilterItem {

    private String name;
    private String type;
    private String id;
    private String content;

    public SearchFilterItem(){}

    SearchFilterItem(String name, String id, String type) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.content = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
