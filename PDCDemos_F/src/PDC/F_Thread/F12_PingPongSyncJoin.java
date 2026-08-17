/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 * @Note the CallMe class comes from previous example
 */
public class F12_PingPongSyncJoin extends Thread {

    private String w;
    static CallMe target;


    public F12_PingPongSyncJoin(String whatToSay) {
        w = whatToSay;
    }

    @Override
    public void run() {
        target.call(w);
    }

    public static void main(String[] args) {
        target = new CallMe();
        Thread ping = new F12_PingPongSyncJoin("ping");
        Thread pong = new F12_PingPongSyncJoin("PONG");
        ping.start();
        try {
            ping.join();
        } catch (InterruptedException e) {
        }
        pong.start();
    }

}
