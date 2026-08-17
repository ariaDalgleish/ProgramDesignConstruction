/*
 * The programs are designed for PDC paper
 */

package PDC.F_Thread_Comm3;

/**
 *
 * @author qbai
 */
public class Shipper implements Runnable {

    Car car;

    Shipper(Car car) {
        this.car = car;
        new Thread(this, "Shipper").start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            car.ship();
        }

    }
}
