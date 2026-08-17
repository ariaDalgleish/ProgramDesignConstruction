/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

import java.util.Random;


/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F02_Extends {

    public static void main(String[] args) {

        DoAnything thread1 = new DoAnything("Do homework");
        DoAnything thread2 = new DoAnything("Watch TV");

        thread1.start();
        thread2.start();

    }
}

//Extends Thread and override run()
class DoAnything extends Thread {

    private final String doThing;
    private final Random rand;

    public DoAnything(String doWhat) {
        this.doThing = doWhat;
        this.rand = new Random();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(rand.nextInt(500));

                //Yield: Causes the currently executing thread object to temporarily pause 
                //Allow other threads to execute
                this.yield();
            } catch (InterruptedException ex) {
                System.out.println("The sleeping has been interruped!");
            }
            System.out.println(doThing + ":  " + i);
        }
    }

}
