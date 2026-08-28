/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Task02_3;

import java.io.*;

public class Input_Output {
    public static void main(String[] args) {
        String inputPath  = "src/Task02_2/T02_input.txt"; // moved txt file into project path so it's easier to find!
        String outputPath = "src/Task02_2/T02_output.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath));
             PrintWriter writer = new PrintWriter(new FileOutputStream(outputPath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                StringBuilder lettersOnly = new StringBuilder();
                for (char c : line.toCharArray()) {
                    if (Character.isLetter(c)) {
                        lettersOnly.append(c);
                    }
                }
                lettersOnly.reverse();
                writer.println(lettersOnly.toString().toUpperCase());
            }

            System.out.println("Done. Output written to " + outputPath);

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            System.out.println("Working directory: " + System.getProperty("user.dir"));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}