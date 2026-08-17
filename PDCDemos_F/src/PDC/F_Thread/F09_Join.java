/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F09_Join {
    //join(long millis): 
    //1. Waits at most millis milliseconds for this thread to finish
    //2. A timeout of 0 means to wait forever until the thread die
    //3. InterruptedException is thrown
    //4. You can only "join" a thread before it is selected by the processer

    public static void main(String[] args) {
        Thread ping = new PingPongJoin("ping", 500);
        Thread pong = new PingPongJoin("PONG", 500);
        ping.start();
        try {
            ping.join(); //No thread can start until ping finishes 
//            ping.join(1000); //wait for 1000 milliseconds for this thread to finish

        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        //If pong starts before ping, join does not work . 
        pong.start();
    }
}

class PingPongJoin extends Thread {

    private final String word;
    private final int delay;

    public PingPongJoin(String whatToSay, int delayTime) {
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
            }
        }
    }

}
