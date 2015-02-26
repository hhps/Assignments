package ua.pp.condor.searchengine.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Document {

    private int id;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document document = (Document) o;
        if (id != document.id) return false;
        return !(text != null ? !text.equals(document.text) : document.text != null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
