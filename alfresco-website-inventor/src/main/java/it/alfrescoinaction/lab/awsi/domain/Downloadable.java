package it.alfrescoinaction.lab.awsi.domain;

import java.io.InputStream;

public class Downloadable {

    private InputStream stream;
    private long contentLength;
    private String mimeType;

    public Downloadable(InputStream stream, long contentLength, String mimeType) {
        this.stream = stream;
        this.contentLength = contentLength;
        this.mimeType = mimeType;
    }

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
