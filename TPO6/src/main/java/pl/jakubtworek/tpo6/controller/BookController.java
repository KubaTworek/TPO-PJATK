package pl.jakubtworek.tpo6.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import pl.jakubtworek.tpo6.model.Book;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "Books", urlPatterns = "/books")
public class BookController extends HttpServlet {

    private final List<Book> books = new ArrayList<>();
    public void init() {
        books.add(new Book(1, "Ksiazka 1", "Autor 1"));
        books.add(new Book(2, "Ksiazka 2", "Autor 2"));
        books.add(new Book(3, "Ksiazka 3", "Autor 3"));
        books.add(new Book(4, "Ksiazka 4", "Autor 4"));
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

        out.println("<ul>");
        for (Book book : books) {
            boolean authorMatched = authorFilter == null || authorFilter.isEmpty() || book.getAuthor().contains(authorFilter);
            boolean titleMatched = titleFilter == null || titleFilter.isEmpty() || book.getTitle().contains(titleFilter);

            if (authorMatched && titleMatched) {
                out.println("<li>");
                out.println("ID: " + book.getIdBook() + "<br>");
                out.println("Title: " + book.getTitle() + "<br>");
                out.println("Author: " + book.getAuthor());
                out.println("</li>");
            }
        }
        out.println("</ul>");

        out.println("</body></html>");
    }

    public void destroy() {
    }
}