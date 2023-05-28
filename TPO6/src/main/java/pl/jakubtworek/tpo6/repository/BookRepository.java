package pl.jakubtworek.tpo6.repository;

import pl.jakubtworek.tpo6.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class BookRepository {
    private Connection connection;

    public BookRepository() {

        String url = "jdbc:derby:database;create=true";
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();

/*            String createTableQuery = "CREATE TABLE Books ("
                    + "id INT PRIMARY KEY,"
                    + "title VARCHAR(100),"
                    + "author VARCHAR(100)"
                    + ")";

            statement.executeUpdate(createTableQuery);

            System.out.println("Table created successfully.");*/

/*            String insertQuery1 = "INSERT INTO Books (id, title, author) VALUES (1, 'Book 1', 'Author 1')";
            String insertQuery2 = "INSERT INTO Books (id, title, author) VALUES (2, 'Book 2', 'Author 2')";
            String insertQuery3 = "INSERT INTO Books (id, title, author) VALUES (3, 'Book 3', 'Author 3')";

            statement.executeUpdate(insertQuery1);
            statement.executeUpdate(insertQuery2);
            statement.executeUpdate(insertQuery3);

            System.out.println("Sample data inserted successfully.");*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> getAllBooks() {
        List<Book> filteredBooks = new ArrayList<>();
        String url = "jdbc:derby:database;create=true";

        try (Connection connection = DriverManager.getConnection(url)) {
            String query = "SELECT * FROM Books";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");

                Book book = new Book(id, title, author);
                filteredBooks.add(book);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredBooks;
    }

    public List<Book> findBooksByAuthor(String authorFilter) {
        List<Book> filteredBooks = new ArrayList<>();
        String url = "jdbc:derby:database;create=true";

        try (Connection connection = DriverManager.getConnection(url)) {
            String query = "SELECT * FROM Books WHERE author LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + authorFilter + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");

                Book book = new Book(id, title, author);
                filteredBooks.add(book);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredBooks;
    }

    public List<Book> findBooksByTitle(String titleFilter) {
        List<Book> filteredBooks = new ArrayList<>();
        String url = "jdbc:derby:database;create=true";

        try (Connection connection = DriverManager.getConnection(url)) {
            String query = "SELECT * FROM Books WHERE title LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + titleFilter + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");

                Book book = new Book(id, title, author);
                filteredBooks.add(book);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredBooks;
    }

    public List<Book> findBooksByAuthorAndTitle(String authorFilter, String titleFilter) {
        List<Book> filteredBooks = new ArrayList<>();
        String url = "jdbc:derby:database;create=true";

        try (Connection connection = DriverManager.getConnection(url)) {
            String query = "SELECT * FROM Books WHERE author LIKE ? AND title LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + authorFilter + "%");
            statement.setString(2, "%" + titleFilter + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");

                Book book = new Book(id, title, author);
                filteredBooks.add(book);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredBooks;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
