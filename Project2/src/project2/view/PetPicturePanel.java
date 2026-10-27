package project2.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import project2.model.PetState;

/**
 * @author aria
 *
 * The pictures are read from the class path once, when the panel is made, and
 * they are kept in an EnumMap. 
 * Name of the pet is written under the picture.
 *
 * Swing calls paintComponent every time the window has to be drawn again, so all of
 * the drawing happens inside that method.
 */
public class PetPicturePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Where the pictures of the pet are on the class path. */
    private static final String IMAGES_FOLDER = "/project2/images/";
    private static final String IMAGE_EXTENSION = ".png";

    // The space around the picture and around the name.
    private static final int MARGIN = 6;

    // The size of the name under the picture.
    private static final int NAME_FONT_SIZE = 14;

    /** The picture of every state of the pet. */
    private final EnumMap<PetState, Image> pictures = new EnumMap<>(PetState.class);

    /** The state the pet is in now. */
    private PetState state = PetState.SLEEPING;

    /** The name that is written under the picture. */
    private String petName;

    /**
     * @param petName the name of the pet, written under the picture
     */
    public PetPicturePanel(String petName) {
        this.petName = petName == null ? "" : petName;

        setBackground(Color.WHITE);
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, NAME_FONT_SIZE));

        loadThePictures();
    }

    /**
     * Reads the picture of every state of the pet from the class path. This
     * is done once, when the panel is made, so that the pictures are ready
     * whenever the window is drawn.
     */
    private void loadThePictures() {
        for (PetState state : PetState.values()) {
            pictures.put(state, readThePicture(state));
        }
    }

    /**
     * Reads one picture from the class path.
     *
     * @param state the state the picture belongs to
     * @return the picture of that state
     */
    private Image readThePicture(PetState state) {
        // Locale.ROOT is used so that the name of the file is the same in
        // every country.
        String path = IMAGES_FOLDER + state.name().toLowerCase(Locale.ROOT) + IMAGE_EXTENSION;

        try (InputStream stream = PetPicturePanel.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("The picture " + path
                        + " is not on the class path.");
            }

            Image picture = ImageIO.read(stream);
            if (picture == null) {
                throw new IllegalStateException("The picture " + path
                        + " could not be read.");
            }
            return picture;
        } catch (IOException e) {
            throw new IllegalStateException("The picture " + path
                    + " could not be read.", e);
        }
    }

    /**
     * Shows the picture of the given state of the pet.
     *
     * @param state the state the pet is in
     */
    public void setState(PetState state) {
        this.state = state;
        repaint();
    }
    
    /**
    * Changes the name that is written under the picture.
    *
    * @param petName the new name of the pet
    */
    public void setPetName(String petName) {
        this.petName = petName == null ? "" : petName;
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
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setFont(getFont());

        // The name is under the picture, so the picture gets the room above it.
        int nameHeight = g2.getFontMetrics().getHeight();
        drawThePicture(g2, nameHeight);
        drawTheName(g2);

        g2.dispose();
    }

    /**
     * Draws the picture of the current state, as big as it fits. The picture
     * is kept in its own shape, so it is not squashed.
     *
     * @param g2          where to draw
     * @param nameHeight  the room the name needs under the picture
     */
    private void drawThePicture(Graphics2D g2, int nameHeight) {
        Image picture = pictures.get(state);
        if (picture == null) {
            return;
        }

        int areaWidth = getWidth() - 2 * MARGIN;
        int areaHeight = getHeight() - 2 * MARGIN - nameHeight;
        if (areaWidth <= 0 || areaHeight <= 0) {
            return;
        }

        double scale = Math.min(
                (double) areaWidth / picture.getWidth(null),
                (double) areaHeight / picture.getHeight(null));

        int width = (int) Math.round(picture.getWidth(null) * scale);
        int height = (int) Math.round(picture.getHeight(null) * scale);

        int x = MARGIN + (areaWidth - width) / 2;
        int y = MARGIN + (areaHeight - height) / 2;
        g2.drawImage(picture, x, y, width, height, null);
    }

    /** Draws the name of the pet under the picture. */
    private void drawTheName(Graphics2D g2) {
        if (petName.isEmpty()) {
            return;
        }

        int x = (getWidth() - g2.getFontMetrics().stringWidth(petName)) / 2;
        int y = getHeight() - MARGIN - g2.getFontMetrics().getDescent();
        g2.drawString(petName, x, y);
    }
}
