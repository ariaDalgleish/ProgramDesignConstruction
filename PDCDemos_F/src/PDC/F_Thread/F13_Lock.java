/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F13_Lock extends Thread {

    private final String whatToSay;
    static CallMe target;

    public F13_Lock(String whatToSay) {
        this.whatToSay = whatToSay;
    }

    @Override
    public void run() {
        target.call(whatToSay);
    }

    public static void main(String[] args) {
        target = new CallMe();
        Thread ping = new F13_Lock("ping");
        Thread pong = new F13_Lock("PONG");

        ping.start();
        pong.start();
    }
}

class CallMeOnebyOne {

    private final Lock messageLock = new ReentrantLock();

    //this method is locked for one thread only
    public void call(String msg) {
        messageLock.lock();
        try {
            System.out.print("[" + msg);
            Thread.sleep(1000);
            System.out.println("]");
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        } finally {
            messageLock.unlock();
        }
    }
}
