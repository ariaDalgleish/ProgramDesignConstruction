package project2.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import project2.model.Event;
import project2.model.PetState;

/**
 * @author aria
 *
 * The window of the calendar pet.
 * Model-View-Controller split
 * The window knows nothing about the database and nothing about the rules of
 * the pet. 
 */
public class PetView extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.ENGLISH);

    // The size of the window.
    private static final int WINDOW_WIDTH = 660;
    private static final int WINDOW_HEIGHT = 520;

    // The size of the square the pet is drawn in.
    private static final int PICTURE_SIZE = 180;

    private final JLabel speechLabel = new JLabel(" ");
    private final JLabel dateLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel(" ");
    private final JTextArea eventsArea = new JTextArea();
    private final JButton wakeButton = new JButton("Wake");
    private final JButton addButton = new JButton("Add event");
    private final JButton renameButton = new JButton("Rename pet");
    private final JButton backButton = new JButton("Back");
    private final JButton todayButton = new JButton("Today");
    private final JButton nextButton = new JButton("Next");
    private final PetPicturePanel petPicture;
    private String petName = "Pet";
    /**
     * @param petName the name of the pet, which is written under its picture
     */
    public PetView(String petName) {
        super("Pet Calendar");

        this.petName = petName;
        this.petPicture = new PetPicturePanel(petName);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildMiddlePanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);
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

        petPicture.setPreferredSize(new Dimension(PICTURE_SIZE, PICTURE_SIZE));
        middle.add(petPicture, BorderLayout.WEST);

        eventsArea.setEditable(false);
        eventsArea.setLineWrap(true);

        middle.add(new JScrollPane(eventsArea), BorderLayout.CENTER);
        return middle;
    }

    /** The buttons along the bottom and the status line under them. */
    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(wakeButton);
        buttons.add(renameButton);
        buttons.add(backButton);
        buttons.add(todayButton);
        buttons.add(nextButton);
        buttons.add(addButton);
        bottom.add(buttons, BorderLayout.CENTER);

        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        bottom.add(statusLabel, BorderLayout.SOUTH);

        return bottom;
    }

    public JButton getWakeButton() {
        return wakeButton;
    }
    
    public JButton getRenameButton() {
        return renameButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getTodayButton() {
        return todayButton;
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public PetPicturePanel getPetPicture() {
        return petPicture;
    }

    /** Day viewed */
    public void showTheDay(LocalDate date) {
        dateLabel.setText(date.format(DATE_FORMATTER).toUpperCase());
    }

    /** Events of the day. */
    public void showTheEvents(List<Event> events) {
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

    //** Shows what the pet is doing. */
    public void showThePetState(PetState state) {
        petPicture.setState(state);

        switch (state) {
            case SLEEPING -> speechLabel.setText(petName + ": \"zzz...\" Click me to wake me up.");
            case AWAKE -> speechLabel.setText(petName + ": \"Hey! \" What's happening today?");
            case ACTIVE -> speechLabel.setText(petName + ": \"Flip!");
        }
    }

    /** Shows a message under the buttons. */
    public void showTheStatus(String message) {
        statusLabel.setText(message);
    }

    /** Show if something went wrong. */
    public void showTheProblem(String message) {
        JOptionPane.showMessageDialog(this, message, "Pet Calendar",
                JOptionPane.WARNING_MESSAGE);
    }
    
    /** Shows the pet's name in the title of the window and in its speech. */
    public void showThePetName(String name) {
        petName = name;
        setTitle("Pet Calendar - " + name);
        petPicture.setPetName(name);
    }

    /**
     * Asks the user to type a name for the pet.
     *
     * @param title   the title of the pop-up
     * @param current the name that is in the box to begin with
     * @return the text typed, or null if the user pressed Cancel
     */
    public String askForPetName(String title, String current) {
        return (String) JOptionPane.showInputDialog(this, "What is your pet's name?",
                title, JOptionPane.QUESTION_MESSAGE, null, null, current);
    }
}
