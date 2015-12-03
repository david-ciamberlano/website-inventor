package it.alfrescoinaction.lab.awsi.domain;

import java.util.List;


public class SiteProperties {

    private String siteId;
    private String themeName;
    private List<SiteProperty> searchFields;
    private List<SiteProperty> metadata;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
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
