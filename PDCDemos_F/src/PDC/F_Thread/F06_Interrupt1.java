/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F06_Interrupt1 {

    public static void main(String args[]) {
        Thread ping = new PingPongInterrupt("ping");
        Thread pong = new PingPongInterrupt("PONG");
        ping.start();
        pong.start();
        pong.interrupt();
    }
}

class PingPongInterrupt extends Thread {

    private String word;

    public PingPongInterrupt(String word) {
        this.word = word;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 2; i++) {
//            System.out.println(i + ": " + word + " isInterrupted? " + isInterrupted());
            //How about changing to the following statement
            System.out.println(i + ": " + word + " Interrupted? " + interrupted());
        }

    }
}
