package project2.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author aria
 *
 * The window of the calendar pet. This first version (Lecture 6) only sets
 * the layout up: the pet's speech and the date at the top, the day's events
 * in the middle, a picture of the pet on the left and the buttons along the
 * bottom.
 *
 * The buttons are not connected to anything yet. They just show a message,
 * because the logic is added in the next stage.
 */
public class PetView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JLabel speechLabel = new JLabel("\"zzz...\" I am asleep.");
    private final JLabel dateLabel = new JLabel("Monday, 26 October");
    private final JTextArea eventsArea = new JTextArea();
    private final JButton wakeButton = new JButton("Wake");
    private final JButton backButton = new JButton("Back");
    private final JButton todayButton = new JButton("Today");
    private final JButton nextButton = new JButton("Next");

    public PetView() {
        super("Pet Calendar");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildMiddlePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        addButtonListeners();
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
        eventsArea.setText("09:30   Team standup\n"
                + "               Room 4B\n"
                + "13:00   Lunch with Sam\n"
                + "               Cafe on the corner");

        middle.add(new JScrollPane(eventsArea), BorderLayout.CENTER);
        return middle;
    }

    /** The four buttons along the bottom. */
    private JPanel buildButtonPanel() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));

        buttons.add(wakeButton);
        buttons.add(backButton);
        buttons.add(todayButton);
        buttons.add(nextButton);
        return buttons;
    }

    /**
     * The buttons are not connected to the pet yet, so each one just says
     * what it will do later. This is the layout stage of the project.
     */
    private void addButtonListeners() {
        wakeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(PetView.this,
                        "The pet will wake up here.");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(PetView.this,
                        "The calendar will go back one day here.");
            }
        });

        todayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(PetView.this,
                        "The calendar will jump back to today here.");
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(PetView.this,
                        "The calendar will go forward one day here.");
            }
        });
    }
}
