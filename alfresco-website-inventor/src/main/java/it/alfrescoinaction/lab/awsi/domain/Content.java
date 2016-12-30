package it.alfrescoinaction.lab.awsi.domain;

import java.util.Map;

public interface Content {

    String getId();
    String getName();
    String getTitle();
    String getDescription();
    String getMimeType();
    ContentType getType();
    int getPriority();
    Map<String,String> getProperties();
    void setProperties (Map<String,String> props);
    Map<String, String> getRenditions();
    void setRenditions(Map<String, String> renditions);
    String getRenditionStreamIdbyType(String type);
}
