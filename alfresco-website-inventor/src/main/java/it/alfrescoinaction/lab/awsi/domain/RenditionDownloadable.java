package it.alfrescoinaction.lab.awsi.domain;

public class RenditionDownloadable implements Downloadable<byte[]> {

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

    @Override
    public boolean hasContent() {
        return contentLenght > 0;
    }
}
