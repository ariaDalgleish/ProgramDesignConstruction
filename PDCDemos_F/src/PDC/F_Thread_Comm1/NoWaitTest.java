/*
 * The programs are designed for PDC paper
 */

package PDC.F_Thread_Comm1;

class NoWaitTest {

    //The first demo shows threads without communications
    public static void main(String args[]) {

        Car car = new Car();
        new Producer(car);
        new Consumer(car);
    }
}
