/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F03_MainThread {

    public static void main(String args[]) throws InterruptedException {
        System.out.println("Current thread: " + Thread.currentThread());
        Thread.currentThread().setName("My Thread");
        
        //think about the output of println(Thread.currentThread())
        //why it's not a class name@ hex hashcode?
        System.out.println("After name change: " + Thread.currentThread());

        Thread thread1 = new CounterThread("Number ONE");
        Thread thread2 = new CounterThread("Number TWO");
        thread1.start();
        thread2.start();

    }
}

class CounterThread extends Thread {

    public String name;

    public CounterThread(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(100);
                //Return the thread object of the current Thread
                System.out.print(Thread.currentThread().getName() + " | ");
                System.out.println("Thread: " + this.name + " ===" + i);

            } catch (InterruptedException ex) {
                System.out.println("The thread has been interrupted!");
            }
        }
    }

}
