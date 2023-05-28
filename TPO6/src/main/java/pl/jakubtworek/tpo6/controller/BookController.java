package pl.jakubtworek.tpo6.controller;

import java.io.*;
import java.util.List;

import pl.jakubtworek.tpo6.model.Book;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import pl.jakubtworek.tpo6.service.BookService;

@WebServlet(name = "Books", urlPatterns = "/books")
public class BookController extends HttpServlet {

    private final BookService bookService;

    public BookController() {
        this.bookService = new BookService();
    }

    @Override
    public void init() {
        // Initialization code if needed
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Books</h1>");

        printFilterForm(out);

        String authorFilter = request.getParameter("author");
        String titleFilter = request.getParameter("title");

        List<Book> filteredBooks = getFilteredBooks(authorFilter, titleFilter);
        printBooks(out, filteredBooks);

        out.println("</body></html>");
    }

    private void printFilterForm(PrintWriter out) {
        out.println("<form method='get' action='/books'>");
        out.println("Filter by author: <input type='text' name='author'><br>");
        out.println("Filter by title: <input type='text' name='title'><br>");
        out.println("<input type='submit' value='Filter'>");
        out.println("</form>");
    }

    private List<Book> getFilteredBooks(String authorFilter, String titleFilter) {
        if (authorFilter == null && titleFilter == null) {
            return bookService.getAllBooks();
        } else if (authorFilter != null && titleFilter == null) {
            return bookService.findBooksByAuthor(authorFilter);
        } else if (authorFilter == null && titleFilter != null) {
            return bookService.findBooksByTitle(titleFilter);
        } else {
            return bookService.findBooksByAuthorAndTitle(authorFilter, titleFilter);
        }
    }

    private void printBooks(PrintWriter out, List<Book> books) {
        out.println("<ul>");
        for (Book book : books) {
            out.println("<li>");
            out.println("ID: " + book.getId() + "<br>");
            out.println("Title: " + book.getTitle() + "<br>");
            out.println("Author: " + book.getAuthor());
            out.println("</li>");
        }
        out.println("</ul>");
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
