/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread_Comm1;

class Car {

    int n;
//    boolean carAvailable = false;
//    static int carFlag = 1;

    synchronized int get() {
        System.out.println("Got Car: " + n);
        return n;
    }

    synchronized void make(int n) {
        this.n = n;
        System.out.println("Made Car: " + n);
    }
}
