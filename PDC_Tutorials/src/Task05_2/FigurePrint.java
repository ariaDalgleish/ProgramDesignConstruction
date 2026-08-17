/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Task05_2;

/**
 *
 * @author tdp6747
 */
public class FigurePrint {

    public static void main(String[] args) {

        Figure fig = new Figure();
        Space ap = new Space(fig);
        Star at = new Star(fig);

        Thread spaceThread = new Thread(ap, "Space");
        spaceThread.start();

        Thread starThread = new Thread(at, "Star");
        starThread.start();
    }
}
