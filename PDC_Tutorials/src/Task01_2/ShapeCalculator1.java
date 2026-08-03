/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java                 to edit this template
 */
package Task01_2;

/**
 *
 * @author tdp6747
 */
public class ShapeCalculator1 {
    
    public static void main(String[] args){
        // Create a Circle instance 
        Circle cirObj = new Circle(2.5);
        // Create Rectangle instance with width 12 and length 16.5
        Rectangle recObj = new Rectangle(12, 16.5);
        Square squObj = new Square(3.3);
        
        // Calculate Areas, both methods are void
        cirObj.calculateArea();
        recObj.calculateArea();
        squObj.calculateArea(); 
        
        //Print the shape name and area 
        cirObj.printInfo();
        recObj.printInfo();
        squObj.printInfo();    
    }
}
