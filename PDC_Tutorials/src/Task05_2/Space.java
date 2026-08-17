/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Task05_2;

/**
 *
 * @author tdp6747
 */
public class Space implements Runnable {

    private Figure fig;
    private int max;

    public Space(Figure fig) {
        this.fig = fig;
        this.max = 9;
    }

    @Override
    public void run() {
        for (int i = 1; i <= max; i++) {
            fig.printSpace(i);
        }
    }
}
