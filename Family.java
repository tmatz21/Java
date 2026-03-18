import java.util.Scanner;
public class Family {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Dear Lovely user: Please tell me your family name: ");
        String familyName = input.nextLine();

        System.out.println("Are you male or female? ");
        System.out.println("1. Male ");
        System.out.println("2. Female ");
        System.out.println("Enter option 1 or 2");
        int choice = input.nextInt();

        if (choice == 1){
            System.out.println("Hello Mr. " + familyName);
        } else if (choice == 2) {
            System.out.println("Hello Mrs. " + familyName);
        } else
            System.out.println("In Valid Choice");
    }
}