package it.alfrescoinaction.lab.awsi.domain;


import java.util.HashMap;
import java.util.Map;

public class ContentImpl implements Content{

    private String id;
    private String name;
    private String mimeType;
    private String thumbnailId;
    private ContentType type;
    private Map<String,String> properties;

    public ContentImpl(String id, String name, String mimeType, ContentType type) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.type = type;

        this.properties = new HashMap<>();
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
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    @Override
    public Map<String, String> getProperties() {
        // defensive copy
        Map<String,String> props = new HashMap<>();
        props.putAll(this.properties);
        return props;
    }

    @Override
    public void setProperties(Map<String, String> props) {
        // defensive copy
        this.properties.putAll(props);
    }
}
