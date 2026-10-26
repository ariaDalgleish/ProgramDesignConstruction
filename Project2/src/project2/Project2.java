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

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                useSystemLookAndFeel();

                try {
                    EventDao eventDao = EventDaoFactory.createEventDao();
                    CalendarService service = new CalendarService(eventDao);
                    Pet pet = new Pet("Mochi");
                    PetView view = new PetView();

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
     * Tries to use the look and feel of the computer. If that is not
     * possible the window still opens, it just looks a little different.
     */
    private static void useSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // There is nothing to do here - the default look and feel is fine.
        }
    }
}
