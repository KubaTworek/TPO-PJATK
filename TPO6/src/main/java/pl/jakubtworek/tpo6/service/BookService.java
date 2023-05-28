package pl.jakubtworek.tpo6.service;

import pl.jakubtworek.tpo6.model.Book;
import pl.jakubtworek.tpo6.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

public class BookService {
    private final BookRepository bookRepository;

    public BookService() {
        bookRepository = new BookRepository();
    }

    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }

    public List<Book> filterBooksByAuthor(String authorFilter) {
        return bookRepository.findBooksByAuthor(authorFilter);
    }

    public List<Book> filterBooksByTitle(String titleFilter) {
        return bookRepository.findBooksByTitle(titleFilter);
    }

    public List<Book> filterBooks(String authorFilter, String titleFilter) {
        return bookRepository.findBooksByAuthorAndTitle(authorFilter, titleFilter);
    }
}
