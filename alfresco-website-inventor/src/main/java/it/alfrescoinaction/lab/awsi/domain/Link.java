package it.alfrescoinaction.lab.awsi.domain;


public class Link {

    private final String id;
    private final String name;

    public Link(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return  name;
    }


}

