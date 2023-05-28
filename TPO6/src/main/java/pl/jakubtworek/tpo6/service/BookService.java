package pl.jakubtworek.tpo6.service;

import pl.jakubtworek.tpo6.model.Book;
import pl.jakubtworek.tpo6.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

public class BookService {
    private final BookRepository bookRepository;
    private final List<Book> books = new ArrayList<>();

    public BookService() {
        bookRepository = new BookRepository();

        books.add(new Book(1, "Ksiazka 1", "Autor 1"));
        books.add(new Book(2, "Ksiazka 2", "Autor 2"));
        books.add(new Book(3, "Ksiazka 3", "Autor 3"));
        books.add(new Book(4, "Ksiazka 4", "Autor 4"));
    }

    public List<Book> filterBooks(String authorFilter, String titleFilter) {
        List<Book> filteredBooks = new ArrayList<>();

        for (Book book : books) {
            boolean authorMatched = authorFilter == null || authorFilter.isEmpty() || book.getAuthor().contains(authorFilter);
            boolean titleMatched = titleFilter == null || titleFilter.isEmpty() || book.getTitle().contains(titleFilter);

            if (authorMatched && titleMatched) {
                filteredBooks.add(book);
            }
        }

        return filteredBooks;
    }
}
