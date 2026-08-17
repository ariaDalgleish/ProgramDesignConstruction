/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F10_Synchronization1 extends Thread {

    private final String whatToSay;
    static CallMe target;

    public F10_Synchronization1(String whatToSay) {
        this.whatToSay = whatToSay;
    }

    @Override
    public void run() {
        target.call(whatToSay);
    }

    public static void main(String[] args) {
        //target is static, and it is shared ALL the instances of this class
        //if this is not static, what will happen? 
        target = new CallMe();

        F10_Synchronization1 ping = new F10_Synchronization1("ping");
//        ping.target = new CallMe();
        F10_Synchronization1 pong = new F10_Synchronization1("PONG");
//        pong.target = new CallMe();

        ping.start();
        pong.start();
    }
}

class CallMe {

    //this call method is common resource to be called by threads
    //synchronized: common resource will block other threads when it is called by a thread
    //try with "synchronized" and without it
    //how about if target is not static? 
    synchronized void call(String msg) { 
        System.out.print("[" + msg);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
        System.out.println("]");
    }
}
