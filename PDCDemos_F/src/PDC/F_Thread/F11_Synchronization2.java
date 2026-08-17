/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F11_Synchronization2 extends Thread {

    private final String whatToSay;
    private final CallMeAgain target;

    public F11_Synchronization2(String whatToSay, CallMeAgain target) {
        this.whatToSay = whatToSay;
        this.target = target;
    }

    @Override
    public void run() {
        target.call(whatToSay);
    }

    public static void main(String[] args) {
        //create CallMeAgain instance
        CallMeAgain target = new CallMeAgain();
        
        //the same instance is shared by both ping and pong
        F11_Synchronization2 ping = new F11_Synchronization2("ping", target);
        F11_Synchronization2 pong = new F11_Synchronization2("PONG", target);

        ping.start();
        pong.start();
    }
}

class CallMeAgain {

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
