package it.alfrescoinaction.lab.awsi.domain;

import java.io.InputStream;

public class FileDownloadable implements Downloadable{

    private final String name;
    private final InputStream stream;
    private final long contentLength;
    private final String mimeType;

    public FileDownloadable(String name, InputStream stream, long contentLength, String mimeType) {
        this.name = name;
        this.stream = stream;
        this.contentLength = contentLength;
        this.mimeType = mimeType;
    }

    @Override
    public String getName() {return name; }

    @Override
    public InputStream getContent() {
        return stream;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }
}
