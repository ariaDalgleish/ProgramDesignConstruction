/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Task02_4;

/**
 *
 * @author aria
 */
import java.io.*;
import java.util.*;

public class T02_4 {

    public static void main(String[] args) {
        String filePath = "src/Task02_4/T02_scores.txt"; 

        // Step 1: Load existing scores into a Map
        Map<String, Integer> scores = loadScores(filePath);

        // Step 2: Interactive loop to add/update scores
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("\nEnter student name (or 'x' to quit): ");
            String name = sc.nextLine().trim();

            if (name.equalsIgnoreCase("x")) {
                break; // exit loop
            }

            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                continue;
            }

            // Check if the record already exists
            if (scores.containsKey(name)) {
                System.out.println(name + " already has a recorded mark of " + scores.get(name) + ".");
                System.out.print("Overwrite this record? (y/n): ");
                String choice = sc.nextLine().trim();

                if (!choice.equalsIgnoreCase("y")) {
                    System.out.println("Skipped. Moving to next input.");
                    continue; // go back to prompt for next name
                }
            }

            // Prompt for the mark
            int mark;
            System.out.print("Enter mark for " + name + ": ");
            try {
                mark = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid mark. Please enter a whole number.");
                continue;
            }

            scores.put(name, mark); // add new record or overwrite existing one
            System.out.println("Recorded: " + name + " = " + mark);
        }

        // Step 3: Save the updated map back to the file
        saveScores(filePath, scores);
        System.out.println("\nAll scores saved. Goodbye!");

        sc.close();
    }

    // Reads existing name-mark pairs from file into a Map
    private static Map<String, Integer> loadScores(String filePath) {
        Map<String, Integer> map = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    String name = parts[0];
                    try {
                        int mark = Integer.parseInt(parts[1]);
                        map.put(name, mark);
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping malformed line: " + line);
                    }
                }
            }
            System.out.println("Loaded " + map.size() + " existing record(s).");

        } catch (FileNotFoundException e) {
            System.out.println("No existing file found. Starting with an empty record.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return map;
    }

    // Writes the full Map of scores back to the file
    private static void saveScores(String filePath, Map<String, Integer> scores) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filePath))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.println(entry.getKey() + " " + entry.getValue());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}