package it.alfrescoinaction.lab.awsi.domain;

public class GenericFile implements Content {

    private String text = "";
    private String id;
    private String name;
    private String description;
    private String mimeType;
    private String thumbnail = "";
    private String url = "";
    private ContentType type = ContentType.GENERIC;

    public GenericFile (String id, String name, String description, String mimeType) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public ContentType getType() {
        return type;
    }

    public String getType2() {
        return "GENERIC";
    }
}
