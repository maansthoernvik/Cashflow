package model.objects;

/**
 * Created by MTs on 30/10/16.
 */
public class Element {
    private int id;

    public Element() {
        id = 0;
    }

    public Element(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Element{" +
                "id=" + id +
                '}';
    }
}
