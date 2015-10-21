package it.alfrescoinaction.lab.awsi.domain;


import java.util.HashMap;
import java.util.Map;

public class ContentImpl implements Content{

    private final String id;
    private final String name;
    private final String mimeType;
    private final ContentType type;
    private final int priority;
    private Map<String,String> properties;
    private Map<String,String> renditions;

    public ContentImpl(String id, String name, String mimeType, ContentType type, int priority) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.type = type;
        this.priority = priority;

        properties = new HashMap<>();
        renditions = new HashMap<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    @Override
    public ContentType getType() {
        return type;
    }

    @Override
    public int getPriority() {
        return priority;
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


    public Map<String, String> getRenditions() {
        // defensive copy
        Map<String,String> renditions = new HashMap<>();
        renditions.putAll(this.renditions);
        return renditions;
    }

    public String getRenditionStreamIdbyType(String type) {
        if (renditions.containsKey(type)) {
            return renditions.get(type);
        }
        else return renditions.get("default");
    }

    public void setRenditions(Map<String, String> renditions) {
        // defensive copy
        this.renditions.putAll(renditions);
    }
}
