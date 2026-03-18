import java.util.Scanner;
public class LargestToSmallest {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        int number;
        int largest = Integer.MIN_VALUE;
        int smallest = Integer.MAX_VALUE;

        System.out.println("Enter Numbers (-99 to stop): ");
        number = input.nextInt();

        while (number != -99) {
            if (number > largest) {
                largest = number;
            }
            if (number < smallest) {
                smallest = number;
            }
            number = input.nextInt();
        }
        System.out.println("The largest number is: " + largest);
        System.out.println ("The smallest number: " + smallest);
        input.close();
    }
}