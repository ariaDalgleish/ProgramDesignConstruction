package project2.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import project2.data.DataAccessException;
import project2.data.EventDao;
import project2.model.Event;

/**
 * @author aria
 *
 * The window of the calendar pet. The layout is the one from Lecture 6, but
 * now the events come out of the database (Lecture 8) and there is an
 * "Add event" button.
 *
 * This version still does too much: it changes the day itself and it talks
 * to the database. Both of those are moved out into the model and the
 * controller in the next stage (Lecture 9).
 */
public class PetView extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.ENGLISH);

    private final EventDao eventDao;
    private LocalDate currentDate;

    private final JLabel speechLabel = new JLabel("\"zzz...\" I am asleep.");
    private final JLabel dateLabel = new JLabel("");
    private final JTextArea eventsArea = new JTextArea();
    private final JButton wakeButton = new JButton("Wake");
    private final JButton backButton = new JButton("Back");
    private final JButton todayButton = new JButton("Today");
    private final JButton nextButton = new JButton("Next");
    private final JButton addButton = new JButton("Add event");

    /**
     * @param eventDao where the events are read from and saved to
     */
    public PetView(EventDao eventDao) {
        super("Pet Calendar");
        this.eventDao = eventDao;
        this.currentDate = LocalDate.now();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildMiddlePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        addButtonListeners();
        showTheDay(currentDate);
    }

    /** The speech and the date, one above the other. */
    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new GridLayout(2, 1));

        speechLabel.setHorizontalAlignment(JLabel.CENTER);
        dateLabel.setHorizontalAlignment(JLabel.CENTER);

        top.add(speechLabel);
        top.add(dateLabel);
        return top;
    }

    /** The pet's picture on the left and the events of the day on the right. */
    private JPanel buildMiddlePanel() {
        JPanel middle = new JPanel(new BorderLayout());

        PetPicturePanel picture = new PetPicturePanel();
        picture.setPreferredSize(new Dimension(170, 170));
        middle.add(picture, BorderLayout.WEST);

        eventsArea.setEditable(false);
        eventsArea.setLineWrap(true);

        middle.add(new JScrollPane(eventsArea), BorderLayout.CENTER);
        return middle;
    }

    /** The buttons along the bottom. */
    private JPanel buildButtonPanel() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));

        buttons.add(wakeButton);
        buttons.add(backButton);
        buttons.add(todayButton);
        buttons.add(nextButton);
        buttons.add(addButton);
        return buttons;
    }

    /** What each of the buttons does. */
    private void addButtonListeners() {
        wakeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                speechLabel.setText("\"Mmmrm...\" I am awake!");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showTheDay(currentDate.minusDays(1));
            }
        });

        todayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showTheDay(LocalDate.now());
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showTheDay(currentDate.plusDays(1));
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                openTheAddEventWindow();
            }
        });
    }

    /**
     * Shows the events of the given day. The events are read from the
     * database every time, so a new event shows up straight away.
     */
    private void showTheDay(LocalDate date) {
        currentDate = date;
        dateLabel.setText(date.format(DATE_FORMATTER).toUpperCase());
        speechLabel.setText("\"Flip!\" I turned the page.");

        List<Event> events;
        try {
            events = eventDao.findByDate(date);
        } catch (DataAccessException e) {
            eventsArea.setText("The events could not be read: " + e.getMessage());
            return;
        }

        if (events.isEmpty()) {
            eventsArea.setText("Oooo, there's um... nothing to do on this day!");
            return;
        }

        StringBuilder text = new StringBuilder();
        for (Event event : events) {
            text.append(event.toString());
            text.append("\n\n");
        }
        eventsArea.setText(text.toString());
    }

    /** Opens the window for typing in a new event. */
    private void openTheAddEventWindow() {
        AddEventDialog dialog = new AddEventDialog(this, eventDao, currentDate);
        dialog.setVisible(true);

        // The window is closed by now, so the day is shown again to pick up
        // the new event.
        showTheDay(currentDate);
    }
}
