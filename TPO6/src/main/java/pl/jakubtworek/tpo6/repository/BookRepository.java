package pl.jakubtworek.tpo6.repository;

import pl.jakubtworek.tpo6.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BookRepository {
    private final String tableName = "Books";
    private final Connection connection;

    public BookRepository() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            String url = "jdbc:derby:database;create=true";
            connection = DriverManager.getConnection(url);
            dropTable();
            if (!isTableExists()) {
                createTable();
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

    public void dropTable() {
        String dropTableQuery = "DROP TABLE " + tableName;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropTableQuery);
            System.out.println("Table dropped successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() throws SQLException {
        String createTableQuery = "CREATE TABLE " + tableName + " ("
                + "id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,"
                + "title VARCHAR(100),"
                + "author VARCHAR(100)"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
            System.out.println("Table created successfully.");
        }
    }

    public List<Book> getAllBooks() {
        return executeQuery();
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

    private List<Book> executeQuery() {
        List<Book> filteredBooks = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Books")) {
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

    public void addBook(Book newBook) {
        String insertQuery = "INSERT INTO " + tableName + " (title, author) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, newBook.getTitle());
            statement.setString(2, newBook.getAuthor());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                newBook.setId(generatedId);
                System.out.println("Book added successfully with ID: " + generatedId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
