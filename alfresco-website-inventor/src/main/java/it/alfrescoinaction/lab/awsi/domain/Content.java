package it.alfrescoinaction.lab.awsi.domain;

interface Content {

    String getId();
    String getName();
    String getDescription();
    String getMimeType();
    String getText();
    String getThumbnail();
    String getUrl();
    ContentType getType();

}
