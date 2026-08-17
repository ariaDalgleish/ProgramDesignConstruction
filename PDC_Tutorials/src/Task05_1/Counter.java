package Task05_1;

/**
 *
 * @author tdp6747
 */
public class Counter implements Runnable {

    int num;

    public static void main(String[] args) {
        Counter oddCounter = new Counter(1);
        Counter evenCounter = new Counter(2);

        Thread oddThread = new Thread(oddCounter);
        Thread evenThread = new Thread(evenCounter);
        //count.printNum();
        oddThread.start();
        evenThread.start();
        
    }

    public Counter(int i) {
        this.num = i;
    }

    @Override
    // Run replaces printNum(), main creates a thread and calls .start
    public void run(){
        for(int j = this.num; j <= 10; j += 2){
            System.out.print(j + " ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){
                System.out.println("Interrupted");
            }
        }
    }

}
