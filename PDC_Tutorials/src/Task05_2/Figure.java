package Task05_2;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author tdp6747
 */
public class Figure {
   
    static int n;
    //define two states
    public static final boolean STAR_PRESSED = true;
    public static final boolean SPACE_PRINTED = false;
    // define a flag
    boolean flag == STAR_PRINTED;
    
    //synchronized: The method (common resource) will block other threads when it is called by a thread
    synchronized void printStar(int n) {
        if (flag != SPACE_PRINTED) {
            try {
                // wait() tells the calling thread to give up the monitor
                // and go to sleep until some other thread calls notify()
                wait();
            } catch
            
        }
        
    }
    

}
