package project2.data;

/**
 * @author aria
 *
 * The Factory. A different kind of EventDao could
 * be put here without changing anything else (the Open/Closed principle).
 */
public class EventDaoFactory {

    /**
     * Creates the EventDao that talks to the embedded database.
     *
     * @throws DataAccessException if the database cannot be opened
     */
    public static EventDao createEventDao() throws DataAccessException {
        return new JdbcEventDao(Database.getInstance().getConnection());
    }
}
