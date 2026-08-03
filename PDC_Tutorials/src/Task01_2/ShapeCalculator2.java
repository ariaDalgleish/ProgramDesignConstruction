package Task01_2;

import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * A command-line interface that lets the user select a shape,
 * enter its dimensions, and prints the shape name and area.
 */
public class ShapeCalculator2 {

    /**
     * Entry point of the program.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please select:");
        System.out.println("(1) Rectangle");
        System.out.println("(2) Circle");
        System.out.println("(3) Square");

        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Not an integer");
            scanner.close();
            return;
        }

        switch (choice) {
            case 1:
                System.out.print("Enter width: ");
                double width = scanner.nextDouble();
                System.out.print("Enter length: ");
                double length = scanner.nextDouble();

                Rectangle recObj = new Rectangle(width, length);
                recObj.calculateArea();
                recObj.printInfo();
                break;

            case 2:
                System.out.print("Enter radius: ");
                double radius = scanner.nextDouble();

                Circle cirObj = new Circle(radius);
                cirObj.calculateArea();
                cirObj.printInfo();
                break;

            case 3:
                System.out.print("Enter side length: ");
                double side = scanner.nextDouble();

                Square squObj = new Square(side);
                squObj.calculateArea();
                squObj.printInfo();
                break;

            default:
                System.out.println("Wrong input");
                break;
        }

        scanner.close();
    }
}