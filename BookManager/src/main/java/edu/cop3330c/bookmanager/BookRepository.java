// BookRepository.java
// D. Singletary
// 11/24/24
// Database code for book manager

package edu.cop3330c.bookmanager;

// add DB imports
import java.sql.*;
import java.util.*;

// utility class for managing DB connection
class DatabaseUtility {

    private static final String JDBC_URL =
            "jdbc:h2:mem:bookdb;DB_CLOSE_DELAY=-1";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}

public class BookRepository {

    public void initializeDatabase() {
        try (Connection connection = DatabaseUtility.getConnection();
             Statement statement = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                     "title VARCHAR(255) NOT NULL, " +
                                     "author VARCHAR(255))";
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(Book book) {
        String sql = "INSERT INTO books (title, author) VALUES (?, ?)";
        try (Connection connection = DatabaseUtility.getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()){
                if (resultSet.next()){
                    book.setId(resultSet.getLong(1));
                }
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author FROM books";

        try (Connection connection = DatabaseUtility.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getLong("id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                books.add(book);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return books;
    }

    public Book findById(Long id) {
        String sql = "SELECT id, title, author FROM books WHERE id = ?";

        try (Connection connection = DatabaseUtility.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Book book = new Book();
                    book.setId(resultSet.getLong("id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor(resultSet.getString("author"));
                    return book;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(Book book) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection connection = DatabaseUtility.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, book.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
