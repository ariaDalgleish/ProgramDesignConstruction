package project2.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aria
 *
 * The pet itself. It has three states - SLEEPING, AWAKE and ACTIVE - and it
 * keeps the day that is being looked at.
 *
 * The pet knows nothing about the window. When something changes it tells
 * the listeners that registered with it (the Observer pattern) and the
 * window decides what to do about it.
 *
 * The idle timer is measured from the last time the pet was used, so a
 * timeout can be given in the constructor. The tests use a short one.
 */
public class Pet {

    private static final long DEFAULT_IDLE_TIMEOUT_MILLIS = 60_000L; // one minute
    private static final String DEFAULT_NAME = "Your pet";

    private final List<PetListener> listeners = new ArrayList<PetListener>();
    private final String name;
    private final long idleTimeoutMillis;

    private PetState state = PetState.SLEEPING;
    private LocalDate currentDate = LocalDate.now();
    private long lastInteractionTime = System.currentTimeMillis();

    public Pet(String name) {
        this(name, DEFAULT_IDLE_TIMEOUT_MILLIS);
    }

    /**
     * @param name              the name of the pet
     * @param idleTimeoutMillis how long the pet waits before it falls asleep
     */
    public Pet(String name, long idleTimeoutMillis) {
        if (name == null || name.trim().isEmpty()) {
            this.name = DEFAULT_NAME;
        } else {
            this.name = name.trim();
        }
        this.idleTimeoutMillis = idleTimeoutMillis;
    }

    public String getName() {
        return name;
    }

    public PetState getState() {
        return state;
    }

    /** The day that is being looked at. */
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /** Sets the day that is being looked at. */
    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public long getIdleTimeoutMillis() {
        return idleTimeoutMillis;
    }

    /** Registers a listener that is told about every state change. */
    public void addListener(PetListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /** Removes a listener, so it is not told anything any more. */
    public void removeListener(PetListener listener) {
        listeners.remove(listener);
    }

    /** Wakes the pet up. */
    public void wake() {
        setState(PetState.AWAKE);
    }

    /**
     * Puts the pet to sleep. The day goes back to today as well, so that
     * waking the pet up later always starts from the current day.
     */
    public void sleep() {
        // The day is set first, so that anybody told about the change
        // already sees the new day and not the one that has just been left.
        currentDate = LocalDate.now();
        setState(PetState.SLEEPING);
    }

    /**
     * The pet has been used. A sleeping pet wakes up and the idle timer
     * starts again.
     */
    public void interact() {
        lastInteractionTime = System.currentTimeMillis();
        if (state == PetState.SLEEPING) {
            setState(PetState.AWAKE);
        }
    }

    /** The pet is carrying out something, so it is ACTIVE for a moment. */
    public void beginActivity() {
        lastInteractionTime = System.currentTimeMillis();
        setState(PetState.ACTIVE);
    }

    /** The pet has finished and is waiting for the next command. */
    public void endActivity() {
        setState(PetState.AWAKE);
    }

    /**
     * Checks whether the pet has been left alone for too long. If it has,
     * the pet falls asleep.
     *
     * @return true if the pet has just fallen asleep
     */
    public boolean checkIdleTimeout() {
        if (state == PetState.SLEEPING || state == PetState.ACTIVE) {
            return false;
        }
        if (millisSinceLastInteraction() > idleTimeoutMillis) {
            sleep();
            return true;
        }
        return false;
    }

    /** How long the pet has been left alone, in milliseconds. */
    public long millisSinceLastInteraction() {
        return System.currentTimeMillis() - lastInteractionTime;
    }

    /** Changes the state and tells the listeners about it. */
    private void setState(PetState newState) {
        if (state == newState) {
            return;
        }
        state = newState;

        // A copy of the list is used, because a listener is allowed to
        // remove itself while it is being told.
        List<PetListener> copy = new ArrayList<PetListener>(listeners);
        for (PetListener listener : copy) {
            listener.stateChanged(newState);
        }
    }
}
