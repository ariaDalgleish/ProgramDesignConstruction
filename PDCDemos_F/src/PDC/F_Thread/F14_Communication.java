/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread;

import java.util.Random;

/**
 *
 * @author Quan Bai and Weihua Li
 * @Note Thread Communication: Producer and Consumer
 * @Note The codes of the example in the slide
 */
public class F14_Communication {

    public static void main(String args[]) {
        Car car = new Car();
        new Producer(car);
        new Consumer(car);
    }
}

class Producer implements Runnable {

    Car car;
    Random random;

    Producer(Car car) {
        this.car = car;
        this.random = new Random();
        new Thread(this, "Producer").start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            car.make(i);

            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

class Consumer implements Runnable {

    Car car;
    Random random;

    Consumer(Car car) {
        this.car = car;
        this.random = new Random();
        new Thread(this, "Consumer").start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            car.get();
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

class Car {

    int n;
    boolean carAvailable = false;

    synchronized int get() {
        if (!carAvailable) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Got Car: " + n);
        carAvailable = false;
        notify();
        return n;
    }

    synchronized void make(int n) {
        if (carAvailable) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        this.n = n;
        System.out.println("Made Car: " + n);
        carAvailable = true;
        notify();
    }
}
