package it.alfrescoinaction.lab.awsi.domain;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class RenditionDownloadable implements Downloadable {

    private final String name;
    private final byte[] content;
    private final long contentLenght;
    private final String mimeType;


    public RenditionDownloadable(String name, byte[] content, long contentLenght, String mimeType) {
        this.name = name;
        this.content = content;
        this.contentLenght = contentLenght;
        this.mimeType =  mimeType;
    }

    @Override
    public String getName() {
        return name;
    }

//    public InputStream getContent() {
//        return new ByteArrayInputStream(content);
//    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public long getContentLength() {
        return contentLenght;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

}
