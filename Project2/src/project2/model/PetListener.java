package project2.model;

/**
 * @author aria
 *
 * The Observer interface for the pet. Anything that wants to know when the
 * pet changes state registers one of these with the pet.
 *
 * The window registers one of these, so that it can redraw itself when the
 * pet wakes up or falls asleep.
 */
public interface PetListener {

    /** Called every time the pet changes state. */
    void stateChanged(PetState newState);
}
