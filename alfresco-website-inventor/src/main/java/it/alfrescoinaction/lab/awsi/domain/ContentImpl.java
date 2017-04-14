package it.alfrescoinaction.lab.awsi.domain;


import java.util.HashMap;
import java.util.Map;

public class ContentImpl implements Content{

    private final String id;
    private final String name;
    private final String title;
    private final String description;
    private final String mimeType;
    private final ContentType type;
    private final int priority;
    private Map<String,String> properties = new HashMap<>(0);
    private Map<String,String> renditions = new HashMap<>(0);

    public ContentImpl(String id, String name, String title, String description,
                       String mimeType, ContentType type, int priority) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.mimeType = mimeType;
        this.type = type;
        this.priority = priority;
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
    public String getTitle() {return this.title;}

    @Override
    public String getDescription() {
        return this.description;
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
        return new HashMap<>(properties);
    }

    @Override
    public void setProperties(Map<String, String> props) {
        // defensive copy
        properties.putAll(props);
    }


    public Map<String, String> getRenditions() {
        // defensive copy
        return new HashMap<>(renditions);
    }

    public void setRenditions(Map<String, String> renditions) {
        // defensive copy
        this.renditions.putAll(renditions);
    }

    public String getRenditionStreamIdbyType(String type) {
        if (renditions.containsKey(type)) {
            return renditions.get(type);
        }
        else return renditions.get("default");
    }


}
