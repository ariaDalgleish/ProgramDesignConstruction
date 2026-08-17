/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread_Comm3;

public class Car {

    int n;
    //boolean carAvailable = false;
    static int carFlag = 1;

    synchronized int get() {

        while (carFlag % 3 != 0) {
            try {
                System.out.println("get is waiting");
                wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }

        }

        System.out.println("Got Car: " + n);
        carFlag++;
        notifyAll();
        return n;
    }

    synchronized void ship() {
        while (carFlag % 3 != 2) {
            try {
                System.out.println("ship is waiting");
                wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }

        }
        this.n = n;
        System.out.println("Shipped Car: " + n);
        carFlag++;
        notifyAll();
    }

    synchronized void make(int n) {
        while (carFlag % 3 != 1) {
            try {
                System.out.println("make is waiting");
                wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }

        }
        this.n = n;
        System.out.println("Made Car: " + n);
        carFlag++;
        notifyAll();
    }
}
