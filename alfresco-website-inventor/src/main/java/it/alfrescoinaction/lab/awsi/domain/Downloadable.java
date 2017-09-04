package it.alfrescoinaction.lab.awsi.domain;


public interface Downloadable<T>  {

    String getName();

    T getContent();

    long getContentLength();

    String getMimeType();

    boolean hasContent();
}
