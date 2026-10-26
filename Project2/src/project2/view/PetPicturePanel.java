package project2.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * @author aria
 *
 * Draw pet with Java2D. 
 * Swing calls paintComponent every time the window has to be drawn again, so all of
 * the drawing happens inside that method.
 *
 * Clicking the picture is how the user wakes the pet up. The click is dealt
 * with by the controller.
 */
public class PetPicturePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int BODY_COLOUR_RED = 244;
    private static final int BODY_COLOUR_GREEN = 193;
    private static final int BODY_COLOUR_BLUE = 123;
    private static final int EYE_SIZE = 8;

    // The space around the pet.
    private static final int BODY_MARGIN = 20;

    // The left ear and the right ear are the same only left ear.
    private static final int EAR_INSET = 10;      // how far the ear is in from the side of the head
    private static final int EAR_WIDTH = 30;      // how wide the bottom of the ear is
    private static final int EAR_TIP_INSET = 20;  // how far the point of the ear is from the side
    private static final int EAR_BASE_OFFSET = 20; // how far down the head the bottom of the ear is
    private static final int EAR_TIP_HEIGHT = 10;  // how far the point of the ear is above the head

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

        int bodySize = Math.min(width, height) - BODY_MARGIN;
        int bodyX = (width - bodySize) / 2;
        int bodyY = (height - bodySize) / 2;

        // The ears.
        g2.setColor(new Color(BODY_COLOUR_RED, BODY_COLOUR_GREEN, BODY_COLOUR_BLUE));
        drawTheEars(g2, bodyX, bodyY, bodySize);

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

    /**
     * Draws the two ears of the pet. The right ear is the left ear the other
     * way round, so the numbers of the left ear are the numbers of the right
     * one as well.
     */
    private void drawTheEars(Graphics2D g2, int bodyX, int bodyY, int bodySize) {
        int leftCorner = bodyX + EAR_INSET;
        int rightCorner = bodyX + bodySize - EAR_INSET - EAR_WIDTH;
        int leftTip = bodyX + EAR_TIP_INSET;
        int rightTip = bodyX + bodySize - EAR_TIP_INSET;
        int bottom = bodyY + EAR_BASE_OFFSET;
        int point = bodyY - EAR_TIP_HEIGHT;

        g2.fillPolygon(new int[]{leftCorner, leftCorner + EAR_WIDTH, leftTip},
                new int[]{bottom, bottom, point}, 3);
        g2.fillPolygon(new int[]{rightCorner + EAR_WIDTH, rightCorner, rightTip},
                new int[]{bottom, bottom, point}, 3);
    }
}
