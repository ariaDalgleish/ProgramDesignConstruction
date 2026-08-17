/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread_Comm3;

class Producer implements Runnable {

    Car car;

    Producer(Car car) {
        this.car = car;
        new Thread(this, "Producer").start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            car.make(i);
        }
    }
}
