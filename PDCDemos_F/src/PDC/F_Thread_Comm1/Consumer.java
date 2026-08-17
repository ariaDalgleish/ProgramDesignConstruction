/*
 * The programs are designed for PDC paper
 */

package PDC.F_Thread_Comm1;

/**
 *
 * @author qbai
 */
class Consumer implements Runnable {

    Car car;

    Consumer(Car car) {
        this.car = car;
        new Thread(this, "Consumer").start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            car.get();
        }
    }
}
