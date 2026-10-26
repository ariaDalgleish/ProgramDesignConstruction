package project2;

import java.awt.EventQueue;

import javax.swing.UIManager;

import project2.view.PetView;

/**
 * @author aria
 *
 * Starts the program. For now it only opens the window. The pet, the model
 * and the controller are added in the next stages.
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
                new PetView().setVisible(true);
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
