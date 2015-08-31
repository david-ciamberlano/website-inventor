package it.alfrescoinaction.lab.awsi.domain;

public class ContentImpl implements Content{

    private String text;
    private String id;
    private String name;
    private String description;
    private String mimeType;
    private String thumbnailId;
    private ContentType type;

    public ContentImpl(String id, String name, String description, String mimeType, ContentType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getThumbnailId() {
        return thumbnailId;
    }

    @Override
    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    @Override
    public ContentType getType() {
        return type;
    }


}
