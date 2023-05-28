package pl.jakubtworek.tpo6.model;

public class Book {
    private int idBook;
    private String title;
    private String author;

    public Book(int idBook, String title, String author) {
        this.idBook = idBook;
        this.title = title;
        this.author = author;
    }

    public int getIdBook() {
        return idBook;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
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
