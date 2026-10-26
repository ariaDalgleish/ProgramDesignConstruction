package project2.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * @author aria
 *
 * A small panel that draws the pet with Java2D. Swing calls
 * paintComponent every time the window has to be drawn again, so all of
 * the drawing happens inside that method.
 *
 * Clicking the picture is how the user wakes the pet up. The click is dealt
 * with by the controller, so this class only knows how to draw.
 */
public class PetPicturePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int BODY_COLOUR_RED = 244;
    private static final int BODY_COLOUR_GREEN = 193;
    private static final int BODY_COLOUR_BLUE = 123;
    private static final int EYE_SIZE = 8;

    private boolean asleep = true;

    public PetPicturePanel() {
        setBackground(Color.WHITE);
    }

    /** Whether the pet is asleep. A sleeping pet has its eyes closed. */
    public void setAsleep(boolean asleep) {
        this.asleep = asleep;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // A copy of the graphics is used so that the settings of this panel
        // do not change the settings of the window around it.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        drawPet(g2);
        g2.dispose();
    }

    /** Draws a round cat with two eyes and a smile. */
    private void drawPet(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();

        int bodySize = Math.min(width, height) - 20;
        int bodyX = (width - bodySize) / 2;
        int bodyY = (height - bodySize) / 2;

        // The ears.
        g2.setColor(new Color(BODY_COLOUR_RED, BODY_COLOUR_GREEN, BODY_COLOUR_BLUE));
        g2.fillPolygon(new int[]{bodyX + 10, bodyX + 40, bodyX + 20},
                new int[]{bodyY + 20, bodyY + 20, bodyY - 10}, 3);
        g2.fillPolygon(new int[]{bodyX + bodySize - 40, bodyX + bodySize - 10, bodyX + bodySize - 20},
                new int[]{bodyY + 20, bodyY + 20, bodyY - 10}, 3);

        // The head.
        g2.fillOval(bodyX, bodyY, bodySize, bodySize);

        // The eyes.
        g2.setColor(Color.BLACK);
        int eyeY = bodyY + bodySize / 3;
        if (asleep) {
            // A sleeping pet has its eyes closed.
            g2.drawArc(bodyX + bodySize / 4 - 2, eyeY, EYE_SIZE + 4, EYE_SIZE + 4, 200, 140);
            g2.drawArc(bodyX + bodySize * 2 / 3 - 2, eyeY, EYE_SIZE + 4, EYE_SIZE + 4, 200, 140);
        } else {
            g2.fillOval(bodyX + bodySize / 4, eyeY, EYE_SIZE, EYE_SIZE);
            g2.fillOval(bodyX + bodySize * 2 / 3, eyeY, EYE_SIZE, EYE_SIZE);
        }

        // The smile.
        g2.drawArc(bodyX + bodySize / 3, bodyY + bodySize / 2,
                bodySize / 3, bodySize / 4, 200, 140);
    }
}
