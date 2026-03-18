import java.util.*;
import java.io.*;
public class SlotMachineFile {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        Random rand = new Random();

        String[] words = {"Cherries", "Oranges", "Plums", "Bells", "Melons", "Bars"};

        double totalEntered = 0;
        double totalWon = 0;

        FileWriter writer = new FileWriter("results.txt");

        String playAgain;

        System.out.println("Slot Machine Game");

        do {
            System.out.print("Enter amount: ");
            double amount = input.nextDouble();
            input.nextLine();

            totalEntered += amount;

            String word1 = words[rand.nextInt(6)];
            String word2 = words[rand.nextInt(6)];
            String word3 = words[rand.nextInt(6)];

            System.out.println("Result: " + word1 + " | " + word2 + " | " + word3);

            double winnings = 0;

            if (word1.equals(word2) && word2.equals(word3)) {
                winnings = amount * 3;
                System.out.println("Three matches! You win 3x!");
            }
            else if (word1.equals(word2) || word1.equals(word3) || word2.equals(word3)) {
                winnings = amount * 2;
                System.out.println("Two matches! You win 2x!");
            }
            else {
                System.out.println("No match. Lose.");
            }

            totalWon += winnings;

            writer.write("Spin: " + word1 + " | " + word2 + " | " + word3 + "\n");
            writer.write("Bet: $" + amount + " | Won: $" + winnings + "\n\n");

            System.out.print("Play again? (yes/no): ");
            playAgain = input.nextLine();

        } while (playAgain.equalsIgnoreCase("yes"));

        // Final results
        System.out.println("\nGame Over!");
        System.out.println("Total entered: $" + totalEntered);
        System.out.println("Total won: $" + totalWon);

        writer.write("FINAL RESULTS\n");
        writer.write("Total Entered: $" + totalEntered + "\n");
        writer.write("Total Won: $" + totalWon + "\n");

        writer.close();
        input.close();
    }
}