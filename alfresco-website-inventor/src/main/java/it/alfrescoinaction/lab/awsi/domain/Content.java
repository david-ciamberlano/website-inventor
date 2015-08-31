package it.alfrescoinaction.lab.awsi.domain;

public interface Content {

    String getId();
    String getName();
    String getDescription();
    String getMimeType();
    String getText();
    void setText(String text);
    String getThumbnailId();
    void setThumbnailId(String id);
    ContentType getType();

}
