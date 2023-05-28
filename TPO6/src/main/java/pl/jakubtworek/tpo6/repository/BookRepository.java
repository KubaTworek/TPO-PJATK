package pl.jakubtworek.tpo6.repository;

import pl.jakubtworek.tpo6.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class BookRepository {
    private final String url = "jdbc:derby:database;create=true";
    private final String tableName = "Books";
    private final Connection connection;

    public BookRepository() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            connection = DriverManager.getConnection(url);
            if (!isTableExists()) {
                createTable();
                insertSampleData();
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTableExists() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, tableName.toUpperCase(), null);
        boolean tableExists = resultSet.next();
        resultSet.close();
        return tableExists;
    }

    private void createTable() throws SQLException {
        String createTableQuery = "CREATE TABLE " + tableName + " ("
                + "id INT PRIMARY KEY,"
                + "title VARCHAR(100),"
                + "author VARCHAR(100)"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
            System.out.println("Table created successfully.");
        }
    }

    private void insertSampleData() throws SQLException {
        String insertQuery1 = "INSERT INTO " + tableName + " (id, title, author) VALUES (1, 'Book 1', 'Author 1')";
        String insertQuery2 = "INSERT INTO " + tableName + " (id, title, author) VALUES (2, 'Book 2', 'Author 2')";
        String insertQuery3 = "INSERT INTO " + tableName + " (id, title, author) VALUES (3, 'Book 3', 'Author 3')";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(insertQuery1);
            statement.executeUpdate(insertQuery2);
            statement.executeUpdate(insertQuery3);
            System.out.println("Sample data inserted successfully.");
        }
    }

    public List<Book> getAllBooks() {
        return executeQuery("SELECT * FROM Books");
    }

    public List<Book> findBooksByAuthor(String authorFilter) {
        String query = "SELECT * FROM Books WHERE author LIKE ?";
        return executeQueryWithParameter(query, "%" + authorFilter + "%");
    }

    public List<Book> findBooksByTitle(String titleFilter) {
        String query = "SELECT * FROM Books WHERE title LIKE ?";
        return executeQueryWithParameter(query, "%" + titleFilter + "%");
    }

    public List<Book> findBooksByAuthorAndTitle(String authorFilter, String titleFilter) {
        String query = "SELECT * FROM Books WHERE author LIKE ? AND title LIKE ?";
        return executeQueryWithTwoParameters(query, "%" + authorFilter + "%", "%" + titleFilter + "%");
    }

    private List<Book> executeQuery(String query) {
        List<Book> filteredBooks = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            addBooksToList(filteredBooks, statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredBooks;
    }

    private List<Book> executeQueryWithParameter(String query, String parameter) {
        List<Book> filteredBooks = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameter);
            addBooksToList(filteredBooks, statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredBooks;
    }

    private List<Book> executeQueryWithTwoParameters(String query, String parameter1, String parameter2) {
        List<Book> filteredBooks = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameter1);
            statement.setString(2, parameter2);
            addBooksToList(filteredBooks, statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredBooks;
    }

    private void addBooksToList(List<Book> filteredBooks, PreparedStatement statement) throws SQLException {
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
