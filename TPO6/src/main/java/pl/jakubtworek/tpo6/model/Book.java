package pl.jakubtworek.tpo6.model;

public class Book {
    private final String title;
    private final String author;
    private int id;

    public Book(int idBook, String title, String author) {
        this.id = idBook;
        this.title = title;
        this.author = author;
    }

    public Book(String title, String author) {
        this.id = 0;
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

    public String getAuthor() {
        return author;
    }
}
