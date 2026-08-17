package Task05_2;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author tdp6747
 */

public class Figure {
    // define two states
    public static final boolean STAR_PRINTED = true;
    public static final boolean SPACE_PRINTED = false;

    // space should print first each row, so flag starts as STAR_PRINTED
    boolean flag = STAR_PRINTED;

    // synchronized: blocks other threads while this method is running
    synchronized void printSpace(int n) {
        if (flag != STAR_PRINTED) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
        for (int i = 0; i < (9 - n); i++) {
            System.out.print(" ");
        }
        flag = SPACE_PRINTED;
        // wakes up the thread waiting to print stars
        notify();
    }

    synchronized void printStar(int n) {
        if (flag != SPACE_PRINTED) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
        for (int i = 0; i < (2 * n - 1); i++) {
            System.out.print("*");
        }
        System.out.println();
        flag = STAR_PRINTED;
        // wakes up the thread waiting to print spaces
        notify();
    }
}
