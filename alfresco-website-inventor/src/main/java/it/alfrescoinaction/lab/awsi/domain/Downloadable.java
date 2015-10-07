package it.alfrescoinaction.lab.awsi.domain;

import java.io.InputStream;

/**
 * Created by david on 10/2/15.
 */
public interface Downloadable<T>  {

    String getName();

    T getContent();

    long getContentLength();

    String getMimeType();
}
