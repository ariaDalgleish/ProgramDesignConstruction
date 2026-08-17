/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 * @Note You will find that the thread is interrupted, but it is actually still
 * running.The interrupt mechanism is implemented using an internal flag known
 * as the interrupt status. Invoking Thread.interrupt sets this flag. When a
 * thread checks for an interrupt by invoking the static method
 * Thread.interrupted, interrupt status is cleared. The non-static isInterrupted
 * method, which is used by one thread to query the interrupt status of another,
 * does not change the interrupt status flag.
 */
public class F07_Interrupt2 extends Thread {

    public static void main(String args[]) throws InterruptedException {
        Thread t1 = new F07_Interrupt2();
        t1.start();
        Thread.sleep(10);
        t1.interrupt(); //interrupt() is an action, interrupted() is checking the status
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("Thread is running == " + i + ", " + isInterrupted());

            //if you would like to stop the thread, you may check if the 
            //isInterrupted() equals to true by using below codes
//            if (isInterrupted() == true) {
//                break;
//            }
        }
    }

}
