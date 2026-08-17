/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F05_PingPongSleep extends Thread {

    private String word;
    private int delay;

    public static void main(String[] args) {
        Thread ping = new F05_PingPongSleep("ping", 500);
        Thread pong = new F05_PingPongSleep("PONG", 1000);
        ping.start();
        pong.start();
    }

    public F05_PingPongSleep(String whatToSay, int delayTime) {
        word = whatToSay;
        delay = delayTime;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.print(word + " ");
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
    }

}
