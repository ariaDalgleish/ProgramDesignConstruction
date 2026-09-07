/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment1;

/**
 *
 * @author aria
 */
public class Main {
    public static void main(String[] args) {
        CalendarSource calendarSource = new LocalFileCalendarSource("calendar.txt");
        Pet pet = new Pet(calendarSource);
        pet.run();
    }
}
