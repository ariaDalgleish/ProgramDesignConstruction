/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Task01_2;

/**
 *
 * @author tdp6747
 */
public class Circle extends Shape{
     double radius;
    
    public Circle (double radius){
        super("Circle");
        this.radius = radius;
    }
    
    @Override
    public void calculateArea(){
        this.area = Math.PI * this.radius * this.radius;
    }
}