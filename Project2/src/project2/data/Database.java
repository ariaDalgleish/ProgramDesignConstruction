package project2.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author aria
 *
 * Keeps the one and only connection to the calendar database (the Singleton
 * pattern).
 *
 * An embedded Derby database only allows one connection to a database while
 * the program is running, so the connection is made once, here, and everybody
 * else asks this class for it with getInstance().
 *
 * Derby also wants to be told where to keep its files before it starts up,
 * so the derby.system.home property is set before the first connection is
 * opened. The folder is "db" inside the folder the program is started from.
 */
public final class Database {

    public static final String DATABASE_NAME = "PetCalendarDB";

    private static final String USER = "pdc";
    private static final String PASSWORD = "pdc";
    private static final String HOME_FOLDER = "db";
    private static final String HOME_PROPERTY = "derby.system.home";

    // The one and only instance of this class. It is empty until somebody
    // asks for it for the first time.
    private static Database instance;

    private final Connection connection;

    /**
     * Opens the connection and makes sure that the table of the calendar is
     * there.
     */
    private Database() throws DataAccessException {
        makeTheDatabaseFolder();

        String url = "jdbc:derby:" + DATABASE_NAME + ";create=true";
        try {
            connection = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("The calendar database could not be opened", e);
        }

        try {
            Schema.createIfNotExists(connection);
            Schema.seedIfEmpty(connection);
        } catch (SQLException e) {
            throw new DataAccessException("The tables of the calendar database could not be made", e);
        }
    }

    /**
     * The one and only instance of this class. The instance is made the first
     * time it is asked for, and not before.
     */
    public static synchronized Database getInstance() throws DataAccessException {
        if (instance == null) {
            instance = new Database();
        }

        return instance;
    }

    /** The connection to the calendar database. */
    public Connection getConnection() {
        return connection;
    }

    /** Closes the connection to the calendar database. */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            // There is nothing to do here - the program is closing anyway.
        }
    }

    /**
     * Makes the folder that Derby keeps its files in. The folder is made
     * here instead of being put in the project by hand, so that the program
     * works straight away.
     */
    private void makeTheDatabaseFolder() throws DataAccessException {
        Path folder = Paths.get(System.getProperty("user.dir"), HOME_FOLDER);

        try {
            Files.createDirectories(folder);
            System.setProperty(HOME_PROPERTY, folder.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new DataAccessException("The database folder " + folder + " could not be made", e);
        }
    }
}
