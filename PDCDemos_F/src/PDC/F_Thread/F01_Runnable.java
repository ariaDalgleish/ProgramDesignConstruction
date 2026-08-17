/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

import java.util.Random;

/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F01_Runnable {

    public static void main(String args[]) {
        DoSomething doHW = new DoSomething("Do homework");
        DoSomething watchTV = new DoSomething("Watch TV");

        Thread hwThread = new Thread(doHW);
        Thread tvThread = new Thread(watchTV);

        //use start()! NOT run()! 
        //What is going to happen if you change start() to run()?
        hwThread.start();
        tvThread.start();
        System.out.println("Main thread finished");

    }
}

//Implement Runnable interface and override run()
class DoSomething implements Runnable {

    private final String doWhat;
    private final Random rand;

    public DoSomething(String aThing) {
        this.doWhat = aThing;
        rand = new Random();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(rand.nextInt(300));
            } catch (InterruptedException ex) {
                System.out.println("The sleeping has been interruped!");
            }
            System.out.println(doWhat + ":  " + i);
        }
    }
}
