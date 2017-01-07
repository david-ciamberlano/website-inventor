package it.alfrescoinaction.lab.awsi.domain.ecm;

import java.io.InputStream;
import java.util.Map;

public class WSIDocument {

    private String id;
    private String name;
    private String title;
    private String description;
    private String mimetype;

    Map<String,String> properties;
    InputStream contentStream;
    long contentStreamLength;

    Map<String,String> renditions;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public InputStream getContentStream() {
        return contentStream;
    }

    public void setContentStream(InputStream contentStream) {
        this.contentStream = contentStream;
    }

    public long getContentStreamLength() {
        return contentStreamLength;
    }

    public void setContentStreamLength(long contentStreamLength) {
        this.contentStreamLength = contentStreamLength;
    }

    public Map<String, String> getRenditions() {
        return renditions;
    }

    public void setRenditions(Map<String, String> renditions) {
        this.renditions = renditions;
    }
}
