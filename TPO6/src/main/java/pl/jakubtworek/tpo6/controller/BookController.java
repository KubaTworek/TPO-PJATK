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

    public void init() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Books</h1>");

        out.println("<form method='get' action='/books'>");
        out.println("Filter by author: <input type='text' name='author'><br>");
        out.println("Filter by title: <input type='text' name='title'><br>");
        out.println("<input type='submit' value='Filter'>");
        out.println("</form>");

        String authorFilter = request.getParameter("author");
        String titleFilter = request.getParameter("title");

        List<Book> filteredBooks;
        if (authorFilter == null && titleFilter == null) {
            filteredBooks = bookService.getAllBooks();
        } else if (authorFilter != null && titleFilter == null) {
            filteredBooks = bookService.filterBooksByAuthor(authorFilter);
        } else if (authorFilter == null && titleFilter != null) {
            filteredBooks = bookService.filterBooksByTitle(titleFilter);
        } else {
            filteredBooks = bookService.filterBooks(authorFilter, titleFilter);
        }

        out.println("<ul>");
        for (Book book : filteredBooks) {
            out.println("<li>");
            out.println("ID: " + book.getIdBook() + "<br>");
            out.println("Title: " + book.getTitle() + "<br>");
            out.println("Author: " + book.getAuthor());
            out.println("</li>");
        }
        out.println("</ul>");

        out.println("</body></html>");
    }

    public void destroy() {
    }
}