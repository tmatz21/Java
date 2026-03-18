import java.util.*;
public class ESPGame {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Random rand = new Random();

        String[] colors = {"Red", "Green", "Blue", "Orange", "Yellow"};
        int correctGuesses = 0;

        System.out.println("ESP Game: Try to guess the color!");

        for (int i = 1; i <= 10; i++) {
            int index = rand.nextInt(5); // Random number 0–4
            String selectedColor = colors[index];

            System.out.print("Round " + i + " - Enter your guess: ");
            String guess = input.nextLine();

            // Compare ignoring case (so "red" = "Red")
            if (guess.equalsIgnoreCase(selectedColor)) {
                correctGuesses++;
            }

            System.out.println("Computer selected: " + selectedColor);
            System.out.println();
        }

        System.out.println("You guessed correctly " + correctGuesses + " times out of 10.");

        input.close();
    }
}