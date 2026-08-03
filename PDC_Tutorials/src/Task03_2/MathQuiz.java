/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Task03_2;

/**
 *
 * @author tdp6747
 */

import java.util.Random;
import java.util.Scanner;

// Modular. Holds state and shows how object references work.
// Demonstrates parameter reassignment vs. field mutation
// Clean UI loop and tolerance for division answers.

public class MathQuiz {
    static class GameState {
        int score = 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        GameState state = new GameState();
        final int totalRounds = 3;
        
        System.out.println("Welcome to Math Quiz Program!");
        System.out.println("Math Quiz — " + totalRounds + " rounds. Type 'x' to quit early. +10 correct, -10 wrong.");
        for (int round = 1; round <= totalRounds; round++) {
            System.out.println("Round " + round + " of " + totalRounds + ":");

            int number1 = 0;
            int number2 = 0;
            char[] operators = {'+', '-', '*', '/'};
            int opIndex = random.nextInt(operators.length);
            char operator = operators[opIndex];

            if (operator == '/') {
                // make integer division questions: choose quotient and divisor so product <=100
                int quotient = random.nextInt(10) + 1; // 1..10
                int maxDivisor = 100 / quotient; // at least 1
                number2 = random.nextInt(maxDivisor) + 1; // 1..maxDivisor
                number1 = number2 * quotient;
            } else {
                number1 = random.nextInt(101);
                number2 = random.nextInt(101);
                if (operator == '-' && number1 < number2) {
                    int tmp = number1; number1 = number2; number2 = tmp;
                }
            }

            double correctAnswer;
            switch (operator) {
                case '+': correctAnswer = number1 + number2; break;
                case '-': correctAnswer = number1 - number2; break;
                case '*': correctAnswer = number1 * number2; break;
                case '/': correctAnswer = (double) number1 / number2; break;
                default: correctAnswer = 0; break;
            }

            System.out.println("Question: " + number1 + " " + operator + " " + number2);

            Double playerAnswer = null;
            while (true) {
                System.out.print("Your answer (or 'x' to quit): ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("x")) {
                    System.out.println("You quit early.");
                    System.out.println("You got: " + state.score);
                    scanner.close();
                    return;
                }
                try {
                    playerAnswer = Double.parseDouble(input);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter a number or 'x' to quit.");
                }
            }

            boolean isCorrect;
            if (operator == '/') {
                isCorrect = Math.abs(playerAnswer - correctAnswer) < 0.01; // tolerance
            } else {
                isCorrect = Math.abs(playerAnswer - correctAnswer) < 0.0001;
            }

            if (isCorrect) {
                state.score += 10;
                System.out.println("Correct! +10 points. Current score: " + state.score);
            } else {
                state.score -= 10;
                if (operator == '/') {
                    System.out.printf("Wrong. Correct answer: %.4f. -10 points. Current score: %d%n", correctAnswer, state.score);
                } else {
                    System.out.printf("Wrong. Correct answer: %.0f. -10 points. Current score: %d%n", correctAnswer, state.score);
                }
            }

            System.out.println();
        }

        System.out.println("Quiz finished.");
        System.out.println("You got: " + state.score);
        scanner.close();
    }
}
