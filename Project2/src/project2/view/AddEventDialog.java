package project2.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import project2.data.DataAccessException;
import project2.data.EventDao;
import project2.model.Event;

/**
 * @author aria
 *
 * The small window where a new event is typed in. The date and the time are
 * typed as text, for example 2026-10-26 and 09:30.
 *
 * If something is wrong with the typing the window stays open and says what
 * is wrong, so the user does not have to start again.
 */
public class AddEventDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm";
    private static final String START_TIME = "09:00";

    private final EventDao eventDao;
    private final JTextField dateField = new JTextField(12);
    private final JTextField timeField = new JTextField(12);
    private final JTextField titleField = new JTextField(20);
    private final JTextField locationField = new JTextField(20);
    private final JLabel messageLabel = new JLabel(" ");

    /**
     * @param owner   the window this dialog belongs to
     * @param eventDao where the new event is saved
     * @param date    the day the new event is for
     */
    public AddEventDialog(Window owner, EventDao eventDao, LocalDate date) {
        super(owner, "Add event", ModalityType.APPLICATION_MODAL);
        this.eventDao = eventDao;

        dateField.setText(date.format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        timeField.setText(START_TIME);

        setLayout(new BorderLayout(8, 8));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(340, getHeight()));
        setLocationRelativeTo(owner);
    }

    /** The boxes the user types into, one row for each part of the event. */
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 4));

        form.add(new JLabel("Date"));
        form.add(dateField);
        form.add(new JLabel("Start time"));
        form.add(timeField);
        form.add(new JLabel("Title"));
        form.add(titleField);
        form.add(new JLabel("Location"));
        form.add(locationField);
        form.add(new JLabel(" "));
        form.add(messageLabel);

        return form;
    }

    /** The Add and Cancel buttons. */
    private JPanel buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                addTheEvent();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        buttons.add(addButton);
        buttons.add(cancelButton);
        return buttons;
    }

    /**
     * Reads what was typed and saves it as a new event. The window only
     * closes when the event has really been saved.
     */
    private void addTheEvent() {
        LocalDate date;
        LocalTime time;

        try {
            date = LocalDate.parse(dateField.getText().trim());
            time = LocalTime.parse(timeField.getText().trim());
        } catch (DateTimeParseException e) {
            messageLabel.setText("The date must look like " + DATE_PATTERN
                    + " and the time like " + TIME_PATTERN + ".");
            return;
        }

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            messageLabel.setText("Please give the event a title.");
            return;
        }

        String location = locationField.getText().trim();

        try {
            eventDao.insert(new Event(date, time, title, location));
        } catch (DataAccessException e) {
            messageLabel.setText("The event could not be saved: " + e.getMessage());
            return;
        }

        dispose();
    }
}
