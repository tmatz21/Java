import java.util.*;
public class EnhancedNumberGuess {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    Random rand = new Random();

    int number = rand.nextInt(4);
    int guess;
    int attempts = 0;

    System.out.println("I Have Generated A Number Between 0 and 3");

    do {
      System.out.print("Enter Your Guess: ");
      guess = input.nextInt();
      attempts++;

      if (guess == number) {
        System.out.println("Well Done. Your Guess Was Right!!!");
        System.out.println("You got it in: " + attempts + " guess(es).");
      }
      else if (guess < number) {
        System.out.println("Try again. Your Guess Was Too Low.");
      }
      else {
        System.out.println("Try again. Your Guess Was Too High");
      }

    } while (guess != number);

    input.close();
  }
}