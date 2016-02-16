package it.alfrescoinaction.lab.awsi.domain;

import java.util.List;


public class SiteProperties {

    private String theme;
    private List<SiteProperty> searchFields;
    private List<SiteProperty> metadata;

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public List<SiteProperty> getSearchFields() {
        return searchFields;
    }

    public void setSearchFields(List<SiteProperty> searchFields) {
        this.searchFields = searchFields;
    }

    public List<SiteProperty> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<SiteProperty> metadata) {
        this.metadata = metadata;
    }
}
