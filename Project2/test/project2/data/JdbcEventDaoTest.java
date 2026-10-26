package project2.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import project2.model.Event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author aria
 *
 * The tests for the real JdbcEventDao. This time the events really are put
 * into a database, but it is a database that lives in the memory of the
 * computer instead of in a file, so nothing is left behind when the tests
 * are finished.
 *
 * Every test starts with an empty database, because the events of the
 * previous test are tidied away before the next test begins.
 */
public class JdbcEventDaoTest {

    private static final String URL = "jdbc:derby:memory:PetCalendarTest;create=true";
    private static final String USER = "pdc";
    private static final String PASSWORD = "pdc";

    // Each test uses a day of its own, so that the tests never meet.
    private static final LocalDate FIRST_DAY = LocalDate.of(2031, 3, 1);
    private static final LocalDate SECOND_DAY = LocalDate.of(2031, 3, 2);
    private static final LocalDate THIRD_DAY = LocalDate.of(2031, 3, 3);
    private static final LocalDate FOURTH_DAY = LocalDate.of(2031, 3, 4);
    private static final LocalDate EMPTY_DAY = LocalDate.of(2031, 3, 5);

    private Connection connection;
    private JdbcEventDao eventDao;

    /**
     * The Derby driver has to be found before any connection can be made.
     *
     * Derby also writes a log file next to the program. The tests ask it to
     * put that log in the temporary folder instead, so that running the
     * tests does not leave anything behind in the project.
     */
    @BeforeClass
    public static void loadTheDatabaseDriver() throws Exception {
        String logFile = System.getProperty("java.io.tmpdir") + File.separator + "derby-test.log";
        System.setProperty("derby.stream.error.file", logFile);

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    /**
     * Every test gets the database, with the table already made and the
     * table emptied out first.
     */
    @Before
    public void openTheDatabase() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        Schema.createIfNotExists(connection);
        emptyTheTable();
        eventDao = new JdbcEventDao(connection);
    }

    /**
     * The database lives in the memory of the computer, but it is still
     * there after the connection has been closed. So the events of the
     * previous test have to be tidied away before the next test starts,
     * otherwise the tests would tread on each other's toes.
     */
    private void emptyTheTable() throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate("DELETE FROM " + Schema.TABLE_NAME);
        } finally {
            statement.close();
        }
    }

    /**
     * The connection is closed at the end of the test, so that the test
     * does not hold the database open.
     */
    @After
    public void closeTheDatabase() throws SQLException {
        connection.close();
    }

    /**
     * Saving an event gives back an event that has an id.
     */
    @Test
    public void insertingAnEventGivesItAnId() throws Exception {
        Event saved = eventDao.insert(new Event(FIRST_DAY, LocalTime.of(9, 0), "Dentist", "Clinic"));

        assertNotNull("the service should give back the saved event", saved);
        assertTrue("the saved event should be saved", saved.isSaved());
        assertTrue("the saved event should have an id", saved.getId() != Event.UNSAVED_ID);
    }

    /**
     * The event that is saved keeps the details it was given.
     */
    @Test
    public void theSavedEventKeepsItsDetails() throws Exception {
        Event saved = eventDao.insert(new Event(FIRST_DAY, LocalTime.of(9, 0), "Dentist", "Clinic"));

        assertEquals("the day should be kept", FIRST_DAY, saved.getDate());
        assertEquals("the time should be kept", LocalTime.of(9, 0), saved.getTime());
        assertEquals("the title should be kept", "Dentist", saved.getTitle());
        assertEquals("the place should be kept", "Clinic", saved.getLocation());
    }

    /**
     * An event that has been saved can be found again by its day.
     */
    @Test
    public void anEventCanBeFoundAgainByItsDay() throws Exception {
        eventDao.insert(new Event(SECOND_DAY, LocalTime.of(9, 0), "Dentist", "Clinic"));

        List<Event> found = eventDao.findByDate(SECOND_DAY);

        assertEquals("there should be one event on that day", 1, found.size());
        assertEquals("the event should be the dentist", "Dentist", found.get(0).getTitle());
        assertEquals("the event should be on the right day", SECOND_DAY, found.get(0).getDate());
    }

    /**
     * Only the events of the day that was asked for are given back.
     */
    @Test
    public void onlyTheEventsOfThatDayAreFound() throws Exception {
        eventDao.insert(new Event(THIRD_DAY, LocalTime.of(9, 0), "Dentist", "Clinic"));
        eventDao.insert(new Event(FOURTH_DAY, LocalTime.of(9, 0), "Cinema", "Rex"));

        List<Event> found = eventDao.findByDate(THIRD_DAY);

        assertEquals("there should be one event on that day", 1, found.size());
        assertEquals("the event should be the dentist", "Dentist", found.get(0).getTitle());
    }

    /**
     * A day that has nothing on it has no events. The list that is given
     * back is an empty one and not a null, because the window always gets a
     * list it can work with.
     */
    @Test
    public void aDayWithNothingOnItHasNoEvents() throws Exception {
        eventDao.insert(new Event(FIRST_DAY, LocalTime.of(9, 0), "Dentist", "Clinic"));

        List<Event> found = eventDao.findByDate(EMPTY_DAY);

        assertNotNull("the list of events should not be null", found);
        assertTrue("there should be no events on that day", found.isEmpty());
    }

    /**
     * The events can be counted.
     */
    @Test
    public void theEventsCanBeCounted() throws Exception {
        int before = eventDao.countAll();

        eventDao.insert(new Event(FIRST_DAY, LocalTime.of(9, 0), "Dentist", "Clinic"));
        eventDao.insert(new Event(SECOND_DAY, LocalTime.of(9, 0), "Cinema", "Rex"));
        eventDao.insert(new Event(FIRST_DAY, LocalTime.of(14, 30), "Quotes", "Home"));

        assertEquals("the three new events should be counted", before + 3, eventDao.countAll());
    }
}
