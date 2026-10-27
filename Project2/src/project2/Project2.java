package project2;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import project2.controller.PetCalendarController;
import project2.data.DataAccessException;
import project2.data.EventDao;
import project2.data.EventDaoFactory;
import project2.model.Pet;
import project2.service.CalendarService;
import project2.view.PetView;

/**
 * @author aria
 *
 * Starts the program. This class only sets the program up: it makes the
 * database source, the pet, the window and the controller, and then it opens
 * the window.
 *
 * Swing asks that the whole window is built on the event thread, which is
 * what EventQueue.invokeLater does.
 */
public class Project2 {

    private static final String FALLBACK_NAME = "Mochi";
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                useSystemLookAndFeel();

                try {
                    EventDao eventDao = EventDaoFactory.createEventDao();
                    CalendarService service = new CalendarService(eventDao);
                    Pet pet = new Pet(chooseTheName());
                    PetView view = new PetView(pet.getName());

                    // The controller connects the three parts together.
                    new PetCalendarController(view, pet, service);

                    view.setVisible(true);
                } catch (DataAccessException e) {
                    // The window cannot be opened without the database, so the
                    // user is told what is wrong instead of the program
                    // stopping without a word.
                    JOptionPane.showMessageDialog(null,
                            "The calendar database could not be opened:\n" + e.getMessage());
                }
            }
        });
    }

    /**
     * use native look and feel 
     */
    private static void useSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // There is nothing to do here - default.
        }
    }
    /**
    * Asks the user for the pet's name before the window opens. An empty name
    * is asked for again. If the user presses Cancel the pet gets a default name.
    */
    private static String chooseTheName() {
        String name = JOptionPane.showInputDialog(null,
                "Can you give me a name for your pet? "
                , "Type any name you wish and hit 'Enter' ... ", JOptionPane.QUESTION_MESSAGE);

        while (name != null && name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Looks like you didn't type anything!");
            name = JOptionPane.showInputDialog(null,
                    "What is your pet's name?", "Name your pet and  hit 'Enter'", JOptionPane.QUESTION_MESSAGE);
        }

        return (name == null) ? FALLBACK_NAME : name;
    }
}
