package io.batizhao.nlp;

/**
 * @author batizhao
 * @since 2019-01-08
 */
public class Document {

    private int id;
    private String title;

    public Document(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
