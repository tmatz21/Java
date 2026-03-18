import java.util.Scanner;
public class XGame {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);

        int number;

        System.out.println("Please enter a positive number 1-15: ");
        number = input.nextInt();

        if (number > 0 && number < 15) {
            for (int i = 1; i <= number; i++) {

                for (int j = 1; j <= number; j++) {
                    System.out.print("X ");
                }

                System.out.println();
            }
        } else {
            System.out.println("Invalid Input. Please Enter A Number Between 1 and 15");
        }
    }
}