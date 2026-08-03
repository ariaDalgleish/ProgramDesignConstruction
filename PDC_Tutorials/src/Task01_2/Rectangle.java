/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Task01_2;

/**
 *
 * @author tdp6747
 */
public class Rectangle extends Shape{
    public double width;
    public double height;
    
    public Rectangle (double width, double height){
        super("Rectangle");
        this.width = width;
        this.height = height;
    }
    /**
     * Constructs a REchtangle with a custom shape name.
     * @param name
     * @param width
     * @param height 
     */
    protected Rectangle(String name, double width, double height){
        super(name);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void calculateArea(){
        this.area = this.width * this.height;
    }
}