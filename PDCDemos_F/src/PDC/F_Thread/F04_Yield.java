/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

/**
 *
 * @author Quan Bai and Weihua Li
 */
public class F04_Yield extends Thread {

    String doThing;

    F04_Yield(String doWhat) {
        this.doThing = doWhat;
    }

    public static void main(String[] args) throws InterruptedException {
        String hw = "do home work";
        String tv = "watch TV";
        F04_Yield thread1 = new F04_Yield(hw);
        F04_Yield thread2 = new F04_Yield(tv);
        
        //can you do thread1.sleep(3000); ? 
        //does it work for thread1?
        
        thread1.start();
        thread2.start();
        
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(doThing + ":  " + i);
            this.yield();
        }
    }

}
