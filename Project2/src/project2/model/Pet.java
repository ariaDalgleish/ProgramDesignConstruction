package project2.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aria
 *
 * The idle timer is measured from the last time the pet was used, so a
 * timeout can be given in the constructor. The tests use a short one.
 */
public class Pet {

    private static final long DEFAULT_IDLE_TIMEOUT_MILLIS = 30_000L; 
    private static final String DEFAULT_NAME = "Your pet";
    private static final int MAX_NAME_LENGTH = 20;

    private final List<PetListener> listeners = new ArrayList<PetListener>();
    private String name;
    private final long idleTimeoutMillis;

    private PetState state = PetState.SLEEPING;
    private LocalDate currentDate = LocalDate.now();
    private long lastInteractionTime = System.currentTimeMillis();

    public Pet(String name) {
        this(name, DEFAULT_IDLE_TIMEOUT_MILLIS);
    }

    /**
     * @param name              
     * @param idleTimeoutMillis how long the pet waits before it falls asleep
     */
    public Pet(String name, long idleTimeoutMillis) {
        this.name = cleanTheName(name);
        this.idleTimeoutMillis = idleTimeoutMillis;
    }

    public String getName() {
         return name;
    }

    public PetState getState() {
       return state;
    }
    /**
    * Gives the pet a new name.
    *
    * @param newName the name the user typed
    * @return false if the name is empty, so nothing changed
    */
    
    public boolean rename(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }   
        name = cleanTheName(newName);
        return true;
    }

    /** Trims the name and cuts it down to the longest name allowed. */
    private static String cleanTheName(String rawName) {
        if (rawName == null || rawName.trim().isEmpty()) {
            return DEFAULT_NAME;
        }
        String clean = rawName.trim();
        if (clean.length() > MAX_NAME_LENGTH) {
            clean = clean.substring(0, MAX_NAME_LENGTH);
        }
        return clean;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /**
     * Sets the day that is being looked at.
     *
     * This is not public on purpose. The day of the calendar is moved with
     * next(), back() and reset(), so that the rule "not while the pet is
     * asleep" cannot be forgotten. Only the pet itself and its own tests use
     * this method.
     */
    void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * @return true if the calendar was moved
     */
    public boolean next() {
        return moveToThisDate(currentDate.plusDays(1));
    }

    /**
     * @return true if the calendar was moved
     */
    public boolean back() {
        return moveToThisDate(currentDate.minusDays(1));
    }

    /**
     * @return true if the calendar was moved
     */
    public boolean reset() {
        return moveToThisDate(LocalDate.now());
    }

    /**
     * Moves the calendar to a day. 
     *
     * @param newDate the day to move to
     * @return true if the calendar was moved
     */
    private boolean moveToThisDate(LocalDate newDate) {
        if (state == PetState.SLEEPING) {
            return false;
        }

        beginActivity();
        currentDate = newDate;
        endActivity();

        return true;
    }

    public long getIdleTimeoutMillis() {
        return idleTimeoutMillis;
    }

    public void addListener(PetListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

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
        currentDate = LocalDate.now();
        setState(PetState.SLEEPING);
    }

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

        List<PetListener> copy = new ArrayList<PetListener>(listeners);
        for (PetListener listener : copy) {
            listener.stateChanged(newState);
        }
    }
}
