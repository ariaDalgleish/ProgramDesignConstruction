/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 * @Note By convention, any method that exits by throwing an
 * InterruptedException clears interrupt status when it does so. However, it's
 * always possible that interrupt status will immediately be set again, by
 * another thread invoking interrupt.
 */
public class F08_Interrupt3 extends Thread {

    public static void main(String args[]) throws InterruptedException {

        Thread t1 = new F08_Interrupt3("Thread Number one");
        t1.start();
        t1.interrupt();

        System.out.println("The interruped status now: " + t1.isInterrupted());

    }

    public F08_Interrupt3(String name) {
        this.setName(name);
    }

    @Override
    public void run() {
        System.out.println("The thread " + this.getName() + " is running...");

        try {
            Thread.sleep(3000);
            System.out.println("The thread " + this.getName() + " Finishes Running...");
        } catch (InterruptedException ex) {
            System.out.println("Thread :" + this.getName() + " has been interrupted!!!");
            System.out.println("Interrupted status in the Catch: " + this.isInterrupted());
        }
    }
}
