/*
 * The programs are designed for PDC paper
 */
package PDC.F_Thread_Comm3;

class NoWaitTest {

    public static void main(String args[]) {

        Car car = new Car();
        new Producer(car);
        new Consumer(car);
        new Shipper(car);
    }
}
