package pl.jakubtworek.tpo6.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.jakubtworek.tpo6.model.Book;
import pl.jakubtworek.tpo6.service.BookService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "Books", urlPatterns = "/books")
public class BookController extends HttpServlet {

    private final BookService bookService;

    public BookController() {
        this.bookService = new BookService();
    }

    @Override
    public void init() {
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("<title>Books</title>");
        out.println("</head><body>");

        out.println("<div class=\"container\">");
        out.println("<h1>Book Library</h1>");

        printAddBookForm(out);
        printFilterForm(out);

        String authorFilter = request.getParameter("author");
        String titleFilter = request.getParameter("title");

        List<Book> filteredBooks = getFilteredBooks(authorFilter, titleFilter);
        printBooks(out, filteredBooks);

        out.println("</div>");

        out.println("</body></html>");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String author = request.getParameter("author");

        Book newBook = new Book(title, author);

        bookService.addBook(newBook);

        response.sendRedirect(request.getContextPath() + "/books");
    }

    private void printFilterForm(PrintWriter out) {
        out.println("<form class='filter-form' method='get' action='/books'>");
        out.println("<label for='author'>Filter by author:</label>");
        out.println("<input type='text' id='author' name='author'><br>");
        out.println("<label for='title'>Filter by title:</label>");
        out.println("<input type='text' id='title' name='title'><br>");
        out.println("<input type='submit' value='Filter'>");
        out.println("</form>");
    }

    private void printAddBookForm(PrintWriter out) {
        out.println("<h2>Add New Book</h2>");
        out.println("<form class=\"add-book-form\" method='post' action='/books'>");
        out.println("<label for='title'>Title:</label>");
        out.println("<input type='text' id='title' name='title'><br>");
        out.println("<label for='author'>Author:</label>");
        out.println("<input type='text' id='author' name='author'><br>");
        out.println("<input type='submit' value='Add Book'>");
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
            out.println("Title: " + book.getTitle() + "<br>");
            out.println("Author: " + book.getAuthor());
            out.println("<form method='delete' action='/books' style='display: inline;'>");
            out.println("<input type='hidden' name='id' value='" + book.getId() + "'>");
            out.println("<input class=\"delete-button\" type='submit' value='X'>");
            out.println("</form>");
            out.println("</li>");
        }
        out.println("</ul>");
    }

    @Override
    public void destroy() {
    }
}
