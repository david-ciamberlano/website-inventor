package it.alfrescoinaction.lab.awsi.exceptions;

public class PageNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 65892587298498L;

    private String pageId;

    public PageNotFoundException(String pageId) {
        this.pageId = pageId;
    }

    public String getPageId() {
        return this.pageId;
    }
}
