package project2.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author aria
 *
 * The tests for the Pet class. The pet is the state machine of the program -
 * it is either asleep, awake or busy - and it has no user interface of its
 * own, so it can be tested by simply calling its methods and looking at the
 * state afterwards.
 *
 * The tests also check that the pet tells its listeners when it changes
 * state, because the window uses that to redraw itself.
 */
public class PetTest {

    // A short timeout, so that the test about falling asleep is quick.
    private static final long SHORT_TIMEOUT = 300L;

    // How long the test waits before it expects the pet to fall asleep.
    private static final long WAIT_AFTER_TIMEOUT = 500L;

    private Pet pet;

    /**
     * Every test gets a pet of its own, so that the tests do not tread on
     * each other's toes.
     */
    @Before
    public void makeANewPet() {
        pet = new Pet("Mochi", SHORT_TIMEOUT);
    }

    /**
     * A pet that has just been made is asleep and waiting to be woken up.
     */
    @Test
    public void aNewPetIsSleeping() {
        assertEquals("a new pet should be sleeping", PetState.SLEEPING, pet.getState());
    }

    /**
     * A pet that has just been made is showing today's date.
     */
    @Test
    public void aNewPetShowsToday() {
        assertEquals("a new pet should show today", LocalDate.now(), pet.getCurrentDate());
    }

    /**
     * The pet keeps the name it was given.
     */
    @Test
    public void thePetKeepsItsName() {
        assertEquals("the pet should keep its name", "Mochi", pet.getName());
    }

    /**
     * Waking the pet up makes it awake.
     */
    @Test
    public void wakingThePetMakesItAwake() {
        pet.wake();

        assertEquals("the pet should be awake", PetState.AWAKE, pet.getState());
    }

    /**
     * Using a pet that is asleep wakes it up.
     */
    @Test
    public void interactingWakesUpASleepingPet() {
        pet.interact();

        assertEquals("the pet should be awake", PetState.AWAKE, pet.getState());
    }

    /**
     * Using a pet that is already awake leaves it awake.
     */
    @Test
    public void interactingLeavesAnAwakePetAwake() {
        pet.wake();

        pet.interact();

        assertEquals("the pet should still be awake", PetState.AWAKE, pet.getState());
    }

    /**
     * When the pet goes back to sleep the date goes back to today as well,
     * so that the pet always wakes up on the current day.
     */
    @Test
    public void sleepingPutsTheDateBackToToday() {
        pet.wake();
        pet.setCurrentDate(LocalDate.of(2020, 1, 1));

        pet.sleep();

        assertEquals("the day should be today again", LocalDate.now(), pet.getCurrentDate());
        assertEquals("the pet should be sleeping", PetState.SLEEPING, pet.getState());
    }

    /**
     * The day that is being looked at can be changed.
     */
    @Test
    public void theDayBeingLookedAtCanBeChanged() {
        pet.setCurrentDate(LocalDate.of(2020, 1, 1));

        assertEquals("the pet should show the day it was given", LocalDate.of(2020, 1, 1), pet.getCurrentDate());
    }

    /**
     * When the pet starts doing something it becomes ACTIVE.
     */
    @Test
    public void beginningAnActivityMakesThePetActive() {
        pet.wake();

        pet.beginActivity();

        assertEquals("the pet should be active", PetState.ACTIVE, pet.getState());
    }

    /**
     * When the pet has finished it goes back to being awake.
     */
    @Test
    public void endingAnActivityMakesThePetAwakeAgain() {
        pet.wake();
        pet.beginActivity();

        pet.endActivity();

        assertEquals("the pet should be awake again", PetState.AWAKE, pet.getState());
    }

    /**
     * A pet that is left alone for longer than the timeout falls asleep.
     */
    @Test
    public void anIdlePetFallsAsleepAfterTheTimeout() throws InterruptedException {
        pet.wake();

        Thread.sleep(WAIT_AFTER_TIMEOUT);
        boolean fellAsleep = pet.checkIdleTimeout();

        assertTrue("the pet should have fallen asleep", fellAsleep);
        assertEquals("the pet should be sleeping now", PetState.SLEEPING, pet.getState());
    }

    /**
     * A pet that has only just been used has not been idle long enough, so
     * it stays awake.
     */
    @Test
    public void aPetThatWasJustUsedStaysAwake() {
        pet.wake();
        pet.interact();

        boolean fellAsleep = pet.checkIdleTimeout();

        assertFalse("the pet should not have fallen asleep", fellAsleep);
        assertEquals("the pet should still be awake", PetState.AWAKE, pet.getState());
    }

    /**
     * A pet that is busy with something does not fall asleep, because it is
     * not waiting for the user.
     */
    @Test
    public void anActivePetDoesNotFallAsleep() throws InterruptedException {
        pet.wake();
        pet.beginActivity();

        Thread.sleep(WAIT_AFTER_TIMEOUT);
        boolean fellAsleep = pet.checkIdleTimeout();

        assertFalse("an active pet should not fall asleep", fellAsleep);
        assertEquals("the pet should still be active", PetState.ACTIVE, pet.getState());
    }

    /**
     * The listeners are told every time the pet changes state.
     */
    @Test
    public void theListenersAreToldAboutStateChanges() {
        RecordingListener listener = new RecordingListener();
        pet.addListener(listener);

        pet.wake();

        assertEquals("the listener should have been told once", 1, listener.numberOfChanges());
        assertEquals("the listener should have been told about the awake state", PetState.AWAKE, listener.lastState());
    }

    /**
     * The listeners are told about the sleeping state as well.
     */
    @Test
    public void theListenersAreToldWhenThePetFallsAsleep() {
        RecordingListener listener = new RecordingListener();
        pet.addListener(listener);
        pet.wake();

        pet.sleep();

        assertEquals("the listener should have been told twice", 2, listener.numberOfChanges());
        assertEquals("the listener should have been told about the sleeping state",
                PetState.SLEEPING, listener.lastState());
    }

    /**
     * A listener is not told about a change that did not happen, so the
     * window does not redraw itself for nothing.
     */
    @Test
    public void theListenersAreNotToldWhenNothingChanges() {
        RecordingListener listener = new RecordingListener();
        pet.addListener(listener);

        pet.wake();
        pet.wake();

        assertEquals("the listener should only have been told once", 1, listener.numberOfChanges());
    }

    /**
     * A listener that has been removed is not told anything any more.
     */
    @Test
    public void aRemovedListenerIsNotTold() {
        RecordingListener listener = new RecordingListener();
        pet.addListener(listener);
        pet.removeListener(listener);

        pet.wake();

        assertEquals("the removed listener should not have been told", 0, listener.numberOfChanges());
    }

    /**
     * A listener that is registered twice is only told once, otherwise the
     * window would redraw itself twice for every change.
     */
    @Test
    public void aListenerIsOnlyRegisteredOnce() {
        RecordingListener listener = new RecordingListener();
        pet.addListener(listener);
        pet.addListener(listener);

        pet.wake();

        assertEquals("the listener should only have been told once", 1, listener.numberOfChanges());
    }

    /**
     * The next day is one day after the day that is being looked at.
     */
    @Test
    public void nextMovesTheCalendarToTheNextDay() {
        pet.wake();
        LocalDate today = LocalDate.now();

        boolean moved = pet.next();

        assertTrue("the calendar should have moved", moved);
        assertEquals("the calendar should be on the next day",
                today.plusDays(1), pet.getCurrentDate());
    }

    /**
     * The day before is one day before the day that is being looked at.
     */
    @Test
    public void backMovesTheCalendarToTheDayBefore() {
        pet.wake();
        LocalDate today = LocalDate.now();

        boolean moved = pet.back();

        assertTrue("the calendar should have moved", moved);
        assertEquals("the calendar should be on the day before",
                today.minusDays(1), pet.getCurrentDate());
    }

    /**
     * A sleeping pet does not move the calendar, the same as in the console
     * version. The method says that nothing happened, so the window can tell
     * the user.
     */
    @Test
    public void nextDoesNothingWhileThePetIsAsleep() {
        LocalDate today = LocalDate.now();

        boolean moved = pet.next();

        assertFalse("the calendar should not have moved", moved);
        assertEquals("the calendar should still be on today", today, pet.getCurrentDate());
    }

    /**
     * The same goes for the day before.
     */
    @Test
    public void backDoesNothingWhileThePetIsAsleep() {
        LocalDate today = LocalDate.now();

        boolean moved = pet.back();

        assertFalse("the calendar should not have moved", moved);
        assertEquals("the calendar should still be on today", today, pet.getCurrentDate());
    }

    /**
     * Resetting the calendar puts it back to today.
     */
    @Test
    public void resetMovesTheCalendarBackToToday() {
        pet.wake();
        pet.setCurrentDate(LocalDate.of(2020, 1, 1));

        boolean moved = pet.reset();

        assertTrue("the calendar should have moved", moved);
        assertEquals("the calendar should be on today", LocalDate.now(), pet.getCurrentDate());
    }

    /**
     * A sleeping pet does not reset the calendar either. The day only goes
     * back to today when the pet falls asleep.
     */
    @Test
    public void resetDoesNothingWhileThePetIsAsleep() {
        pet.setCurrentDate(LocalDate.of(2020, 1, 1));

        boolean moved = pet.reset();

        assertFalse("the calendar should not have moved", moved);
        assertEquals("the calendar should still be on the day it was given",
                LocalDate.of(2020, 1, 1), pet.getCurrentDate());
    }

    /**
     * A listener that simply writes down every state it is told about, so
     * that the test can look at them afterwards.
     */
    private static class RecordingListener implements PetListener {

        private final List<PetState> states = new ArrayList<PetState>();

        @Override
        public void stateChanged(PetState newState) {
            states.add(newState);
        }

        /** How many changes the listener has been told about. */
        int numberOfChanges() {
            return states.size();
        }

        /** The state the listener was told about last. */
        PetState lastState() {
            assertFalse("the listener should have been told about a state", states.isEmpty());
            return states.get(states.size() - 1);
        }
    }
}
