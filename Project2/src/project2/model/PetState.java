package project2.model;

/**
 * @author aria
 */
public enum PetState {
    SLEEPING, //the pet is asleep and waiting to be woken up.
    AWAKE, // the pet is up and the calendar is being looked at.
    ACTIVE /* the pet is carrying out a command. Entering this state also
             restarts the idle timer. */
}
