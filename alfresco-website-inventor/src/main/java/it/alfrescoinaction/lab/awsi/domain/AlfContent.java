package it.alfrescoinaction.lab.awsi.domain;


public class AlfContent {

    private String id;
    private String title;
    private AlfContentType type = AlfContentType.ATTACHMENT;
    private String text;
    private String imgUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AlfContentType getType() {
        return type;
    }

    public void setType(AlfContentType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
