import java.util.*;

public class DiceGame {
    public static void main(String[] args) {
        Random rand = new Random();

        int userWins = 0;
        int computerWins = 0;

        System.out.println("Dice Game: User vs Computer\n");

        for (int i = 1; i <= 10; i++) {
            int computerDie = rand.nextInt(6) + 1; // 1–6
            int userDie = rand.nextInt(6) + 1;     // 1–6

            System.out.println("Round " + i + ":");
            System.out.println("Computer rolled: " + computerDie);
            System.out.println("User rolled: " + userDie);

            if (computerDie > userDie) {
                computerWins++;
                System.out.println("Computer wins this round!");
            }
            else if (userDie > computerDie) {
                userWins++;
                System.out.println("User wins this round!");
            }
            else {
                System.out.println("It's a tie!");
            }

            System.out.println();
        }

        // Final Results
        System.out.println("Final Score:");
        System.out.println("User wins: " + userWins);
        System.out.println("Computer wins: " + computerWins);

        if (userWins > computerWins) {
            System.out.println("You are the grand winner!");
        }
        else if (computerWins > userWins) {
            System.out.println("The computer is the grand winner!");
        }
        else {
            System.out.println("It's an overall tie!");
        }
    }
}