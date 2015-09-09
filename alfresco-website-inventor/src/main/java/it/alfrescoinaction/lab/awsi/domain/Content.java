package it.alfrescoinaction.lab.awsi.domain;

import java.util.Map;

public interface Content {

    String getId();
    String getName();
    String getMimeType();
    String getThumbnailId();
    void setThumbnailId(String id);
    ContentType getType();
    Map<String,String> getProperties();
    void setProperties (Map<String,String> props);
}
