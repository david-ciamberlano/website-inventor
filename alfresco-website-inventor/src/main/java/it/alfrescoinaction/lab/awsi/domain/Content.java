package it.alfrescoinaction.lab.awsi.domain;

public interface Content {

    String getId();
    String getName();
    String getDescription();
    String getMimeType();
    String getText();
    String getThumbnailId();
    String getUrl();
    ContentType getType();
    String getType2();

}
