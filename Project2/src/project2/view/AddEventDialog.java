package project2.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import project2.data.DataAccessException;
import project2.service.CalendarService;
import project2.service.ValidationException;

/**
 * @author aria
 *
 * The small window where a new event is typed in. The date and the time are
 * typed as text, for example 2026-10-26 and 09:30.
 *
 * The window hands the typing to the service 
 * If the service finds a problem the window stays open and says
 * what is wrong, so the user does not lose what they have typed.
 */
public class AddEventDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm";
    private static final String START_TIME = "09:00";

    // The size of the small window.
    private static final int DIALOG_WIDTH = 340;
    private static final int DIALOG_GAP = 8;

    // The size of the boxes. 
    //The date and the time are short, the title and the place are longer.
    private static final int SHORT_FIELD_COLUMNS = 12;
    private static final int LONG_FIELD_COLUMNS = 20;

    // The form has one row for each part of the event, 
    // Label and a box side by side.
    private static final int FORM_ROWS = 5;
    private static final int FORM_COLUMNS = 2;
    private static final int FORM_GAP_X = 8;
    private static final int FORM_GAP_Y = 4;

    private final CalendarService service;
    private final JTextField dateField = new JTextField(SHORT_FIELD_COLUMNS);
    private final JTextField timeField = new JTextField(SHORT_FIELD_COLUMNS);
    private final JTextField titleField = new JTextField(LONG_FIELD_COLUMNS);
    private final JTextField locationField = new JTextField(LONG_FIELD_COLUMNS);
    private final JLabel messageLabel = new JLabel(" ");

    /**
     * @param owner  the window this dialog belongs to
     * @param service where the new event is checked and saved
     * @param date   the day the new event is for
     */
    public AddEventDialog(Window owner, CalendarService service, LocalDate date) {
        super(owner, "Add event", ModalityType.APPLICATION_MODAL);
        this.service = service;

        dateField.setText(date.format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        timeField.setText(START_TIME);

        setLayout(new BorderLayout(DIALOG_GAP, DIALOG_GAP));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(DIALOG_WIDTH, getHeight()));
        setLocationRelativeTo(owner);
    }

    /** The boxes the user types into, one row for each part of the event. */
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(FORM_ROWS, FORM_COLUMNS, FORM_GAP_X, FORM_GAP_Y));

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

        addButton.addActionListener(event -> addTheEvent());

        cancelButton.addActionListener(event -> dispose());

        buttons.add(addButton);
        buttons.add(cancelButton);
        return buttons;
    }

    /**
     * Reads what was typed and asks the service to save it. 
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
        String location = locationField.getText().trim();

        try {
            service.addEvent(date, time, title, location);
        } catch (ValidationException e) {
            messageLabel.setText(buildTheMessage(e.getProblems()));
            return;
        } catch (DataAccessException e) {
            messageLabel.setText("The event could not be saved: " + e.getMessage());
            return;
        }

        dispose();
    }

    /** Puts all the problems under each other, so that they can all be seen. */
    private String buildTheMessage(List<String> problems) {
        StringBuilder message = new StringBuilder("<html>");
        for (String problem : problems) {
            message.append(problem);
            message.append("<br>");
        }
        message.append("</html>");
        return message.toString();
    }
}
