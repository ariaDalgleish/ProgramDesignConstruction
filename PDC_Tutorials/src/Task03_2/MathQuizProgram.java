/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * 
 * @author tdp6747
 */
+

import java.util.Random;









+

import java.util.Scanner;









+











+

public class MathQuizProgram {









+

    public static void main(String[] args) {









+

        Scanner scanner = new Scanner(System.in);









+

        Random rand = new Random();









+

        int score = 0;









+

        System.out.println("Welcome to Math Quiz Program!");









+

        System.out.println("Type 'x' to quit anytime. Correct answer: +10 points, Wrong: -10 points.");









+











+

        while (true) {









+

            int a = rand.nextInt(101); // 0..100









+

            int b = rand.nextInt(101); // 0..100









+

            char[] ops = {'+', '-', '*', '/'};









+

            char op = ops[rand.nextInt(ops.length)];









+











+

            // avoid division by zero









+

            if (op == '/') {









+

                while (b == 0) {









+

                    b = rand.nextInt(101);









+

                }









+

            }









+











+

            String question = String.format("%d %c %d", a, op, b);









+

            System.out.println("Question: " + question);









+











+

            Double userAnswer = null;









+

            while (true) {









+

                System.out.print("Your answer (or 'x' to quit): ");









+

                String line = scanner.nextLine().trim();









+

                if (line.equalsIgnoreCase("x")) {









+

                    System.out.println("Exiting game.");









+

                    System.out.println("Total score: " + score);









+

                    scanner.close();









+

                    return;









+

                }









+

                try {









+

                    userAnswer = Double.parseDouble(line);









+

                    break;









+

                } catch (NumberFormatException e) {









+

                    System.out.println("Invalid input. Please enter a number or 'x' to quit.");









+

                }









+

            }









+











+

            double correctAnswer;









+

            switch (op) {









+

                case '+': correctAnswer = a + b; break;









+

                case '-': correctAnswer = a - b; break;









+

                case '*': correctAnswer = a * b; break;









+

                case '/': correctAnswer = (double) a / b; break;









+

                default: correctAnswer = 0; break;









+

            }









+











+

            boolean isCorrect;









+

            if (op == '/') {









+

                // allow small tolerance for floating point answers









+

                isCorrect = Math.abs(userAnswer - correctAnswer) < 0.01;









+

            } else {









+

                // for integer ops, compare as whole numbers









+

                isCorrect = Math.abs(userAnswer - correctAnswer) < 0.0001;









+

            }









+











+

            if (isCorrect) {









+

                score += 10;









+

                System.out.println("Correct! +10 points. Current score: " + score);









+

            } else {









+

                score -= 10;









+

                System.out.printf("Wrong. Correct answer: %.4f. -10 points. Current score: %d%n", correctAnswer, score);









+

            }









+











+

            System.out.println();









+

        }









+

    }









+

}
