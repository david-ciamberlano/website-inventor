package it.alfrescoinaction.lab.awsi.domain;

import java.util.List;


public class SiteProperties {

    private String theme;
    private List<SiteProperty> metadata;

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public List<SiteProperty> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<SiteProperty> metadata) {
        this.metadata = metadata;
    }
}
