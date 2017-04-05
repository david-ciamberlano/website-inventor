package it.alfrescoinaction.lab.awsi.exceptions;


public class ObjectNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 3584325872912318L;

    private String pageId;

    public ObjectNotFoundException(String pageId) {
        this.pageId = pageId;
    }

    public String getPageId() {
        return this.pageId;
    }
}
