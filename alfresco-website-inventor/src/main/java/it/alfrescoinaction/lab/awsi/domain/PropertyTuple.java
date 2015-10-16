package it.alfrescoinaction.lab.awsi.domain;

/**
 * Created by david on 10/13/15.
 */
public class PropertyTuple {

    private String label;
    private String id;
    private String type;

    public PropertyTuple(String tuple) {
        if (!tuple.isEmpty()) {
            String[] attr = tuple.split("\\|");
           this.label = attr[0];
           this.id = attr[1];
           this.type = attr[2];
        }
        else {
            this.label = "";
            this.id = "";
            this.type = "";
        }
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

}
