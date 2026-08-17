/*
 * The programs are designed for PDC paper
 */

package PDC.F_Thread_Comm2;

class Car {

    int n;
    boolean carAvailable = false;
    static int carFlag = 1;

    synchronized int get() {

        if (!carAvailable) {
            try {
                System.out.println("get is waiting");
                wait();
                System.out.println("get has finished waiting");

            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
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
                System.out.println("make is waiting");
                wait();
                System.out.println("make has finished waiting");

            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }

        }
        this.n = n;
        System.out.println("Made Car: " + n);
        carAvailable = true;
        notify();
    }
}
