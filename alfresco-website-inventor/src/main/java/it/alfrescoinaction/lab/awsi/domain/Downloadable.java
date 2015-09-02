package it.alfrescoinaction.lab.awsi.domain;

import java.io.InputStream;

public class Downloadable {

    private String name;
    private InputStream stream;
    private long contentLength;
    private String mimeType;

    public Downloadable(String name, InputStream stream, long contentLength, String mimeType) {
        this.name = name;
        this.stream = stream;
        this.contentLength = contentLength;
        this.mimeType = mimeType;
    }

    public String getName() {return name; }

    public InputStream getStream() {
        return stream;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getMimeType() {
        return mimeType;
    }
}
