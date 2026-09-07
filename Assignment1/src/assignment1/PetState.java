/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment1;

/**
 *
 * @author aria
 *
 * The three states the desktop pet can be in.
 *
 * SLEEPING - low activity, waiting for the user to wake the pet.
 * AWAKE    - pet is up, calendar is shown, waiting for a command.
 * ACTIVE   - pet is carrying out a command (flip page, reset, etc).
 *            Entering this state also resets the idle timer.
 */
public enum PetState {
    SLEEPING,
    AWAKE,
    ACTIVE
}
