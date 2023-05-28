package pl.jakubtworek.tpo6.model;

public class Book {
    private int id;
    private String title;
    private String author;

    public Book(int idBook, String title, String author) {
        this.id = idBook;
        this.title = title;
        this.author = author;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
