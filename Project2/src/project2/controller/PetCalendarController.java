package project2.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BooleanSupplier;

import javax.swing.Timer;

import project2.data.DataAccessException;
import project2.model.Event;
import project2.model.Pet;
import project2.model.PetListener;
import project2.model.PetState;
import project2.service.CalendarListener;
import project2.service.CalendarService;
import project2.view.AddEventDialog;
import project2.view.PetView;

/**
 * @author aria
 *
 * The controller. It connects the window to the pet and to the service: the
 * clicks on the buttons become calls on the pet, and the changes of the pet
 * and of the events become updates of the window.
 *
 * This is the only class that knows about all three parts of the program,
 * which is what the Model-View-Controller pattern is about.
 */
public class PetCalendarController implements CalendarListener, PetListener {

    private static final int TIMER_INTERVAL_MILLIS = 1000; // checked once a second

    private final PetView view;
    private final Pet pet;
    private final CalendarService service;
    private final Timer timer;

    /**
     * @param view    the window
     * @param pet     the pet the window shows
     * @param service where the events are read from and saved to
     */
    public PetCalendarController(PetView view, Pet pet, CalendarService service) {
        this.view = view;
        this.pet = pet;
        this.service = service;
        this.timer = makeTheTimer();

        // The controller is the listener of both the pet and the service, so
        // it hears about every change (the Observer pattern).
        pet.addListener(this);
        service.addListener(this);

        addButtonListeners();
        addPictureListener();

        timer.start();
        showTheDay();
    }

    /** Called when the events in the database change. */
    @Override
    public void eventsChanged() {
        showTheDay();
    }

    /** Called when the pet changes state. */
    @Override
    public void stateChanged(PetState newState) {
        view.showThePetState(newState);
    }

    /**
     * What each of the buttons does. The lambda shorthand is used here, so
     * that the five buttons do not all look the same: it says the same thing
     * as the long form of a listener, it is just shorter.
     */
    private void addButtonListeners() {
        view.getWakeButton().addActionListener(event -> {
            pet.wake();
            showTheDay();
        });

        view.getBackButton().addActionListener(event -> moveTheDay(pet::back));

        view.getTodayButton().addActionListener(event -> moveTheDay(pet::reset));

        view.getNextButton().addActionListener(event -> moveTheDay(pet::next));

        view.getAddButton().addActionListener(event -> openTheAddEventWindow());
    }

    /**
     * Clicking the pet wakes it up. This one listener stays in the long
     * form, because the mouse listener has several methods in it and the
     * lambda shorthand can only be used with a listener of one method.
     */
    private void addPictureListener() {
        view.getPetPicture().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                pet.interact();
                showTheDay();
            }
        });
    }

    /**
     * Makes the timer that watches the idle timer of the pet. The timer
     * runs on the Swing thread, so there is no sleeping loop anywhere.
     */
    private Timer makeTheTimer() {
        Timer newTimer = new Timer(TIMER_INTERVAL_MILLIS, event -> {
            if (pet.checkIdleTimeout()) {
                // The pet has fallen asleep, so the day has gone back to
                // today and the window has to show it.
                showTheDay();
            }
        });

        return newTimer;
    }

    /**
     * Moves the calendar, if the pet lets it. The pet says whether it did
     * anything, so the rule "the calendar does not move while the pet is
     * asleep" is kept in one place and the three buttons that move the
     * calendar do not each need their own copy of it.
     *
     * @param move what is to be done: the day before, today or the day after
     */
    private void moveTheDay(BooleanSupplier move) {
        if (!move.getAsBoolean()) {
            view.showTheStatus("Wake the pet up first.");
            return;
        }

        showTheDay();
    }

    /** Opens the window for typing in a new event. */
    private void openTheAddEventWindow() {
        AddEventDialog dialog = new AddEventDialog(view, service, pet.getCurrentDate());
        dialog.setVisible(true);
    }

    /** Shows the day that is being looked at and the events on it. */
    private void showTheDay() {
        LocalDate day = pet.getCurrentDate();

        view.showTheDay(day);
        view.showThePetState(pet.getState());

        try {
            List<Event> events = service.eventsOn(day);
            view.showTheEvents(events);
            view.showTheStatus(service.countEvents() + " events are stored in the database.");
        } catch (DataAccessException e) {
            view.showTheProblem("The events could not be read: " + e.getMessage());
        }
    }
}
