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
 * Connects the window to the pet and service
 * buttons call on the pet, changes of pet and events updates the window.
 *
 * This is the only class that knows of all three parts of the program,
 * the Model-View-Controller pattern.
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
        view.showThePetName(pet.getName());
        showTheDay();
    }

    // Called when the events in the database change.
    @Override
    public void eventsChanged() {
        showTheDay();
    }

    // Called when the pet changes state. 
    @Override
    public void stateChanged(PetState newState) {
        view.showThePetState(newState);
    }

    // What each of the buttons does.
    private void addButtonListeners() {
        view.getWakeButton().addActionListener(event -> {
            pet.wake();
            showTheDay();
        });
        
        view.getRenameButton().addActionListener(event -> renameThePet());

        view.getBackButton().addActionListener(event -> moveTheDay(pet::back));

        view.getTodayButton().addActionListener(event -> moveTheDay(pet::reset));

        view.getNextButton().addActionListener(event -> moveTheDay(pet::next));

        view.getAddButton().addActionListener(event -> openTheAddEventWindow());
    }

    // Clicking the pet wakes it up.
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
     * Timer runs on the Swing thread, so there is no sleeping loop anywhere.
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
    
    /** Asks for a new name and gives it to the pet. */
    private void renameThePet() {
        String newName = view.askForPetName("Rename pet", pet.getName());

        if (newName == null) {
            return; // Cancel was pressed, so nothing changes.
        }

        if (!pet.rename(newName)) {
            view.showTheStatus("The name cannot be empty.");
            return;
        }

        view.showThePetName(pet.getName());
        view.showThePetState(pet.getState());
        view.showTheStatus("The pet is now called " + pet.getName() + ".");
    }

    /**
     * Moves the calendar, if the pet lets it. 
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
