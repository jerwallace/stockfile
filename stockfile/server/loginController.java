/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.server;

import java.io.Console;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import stockfile.client.UserSession;
import stockfile.exceptions.CreateUserException;
import stockfile.exceptions.CreateUserException.CreateUserError;
import stockfile.exceptions.InvalidAuthenticationException;
import stockfile.security.AuthenticateService;
import stockfile.security.RegexHelper;
import stockfile.security.RegexHelper.RegExPattern;

/**
 *
 * @author MrAtheist
 */
public class loginController {

    private static void createUser() throws CreateUserException {

        Scanner scanner = new Scanner(System.in);
        Console console = System.console();

        String[] arr = {"Username", "Password", "First name", "Last name", "Email"};
        CreateUserError[] err = {CreateUserException.CreateUserError.INVALID_USERNAME,
            CreateUserException.CreateUserError.PASSWORD,
            CreateUserException.CreateUserError.INVALID_FIRSTNAME,
            CreateUserException.CreateUserError.INVALID_LASTNAME,
            CreateUserException.CreateUserError.EMAIL};
        RegExPattern[] reg = {RegexHelper.RegExPattern.USERNAME,
            RegexHelper.RegExPattern.PASSWORD,
            RegexHelper.RegExPattern.ALPHABETS,
            RegexHelper.RegExPattern.ALPHABETS,
            RegexHelper.RegExPattern.EMAIL};

        String[] ret = new String[5];
        String tmp;

        /*  once migrate to ec2, uncomment the next block
         String username = console.readLine("Username: ");
         char[] password = console.readPassword("Password: ");
         String firstnam = console.readLine("First name: ");
         String lastname = console.readLine("Last name: ");
         String email = console.readLine("Email: ");
        
         System.out.println(new String(password));
         */

        for (int i = 0; i < arr.length; i++) {
            do {  // Loop until we have correct input

                System.out.print(arr[i] + ": ");

                try {
                    tmp = scanner.nextLine();         // Blocks for user input
                    if (!RegexHelper.validate(tmp, reg[i]) || tmp.length() == 0) {
                        throw new CreateUserException(err[i]);
//                        continue;                       // restart loop, wrong input
                    } else {
                        ret[i] = tmp;
                        break;                          // Got valid input, stop looping
                    }
                } catch (final CreateUserException e) {
//                    System.out.println("Invalid input; please try again.\n");
                    //scanner.nextLine();                 // discard non-int input
                    continue;                           // restart loop, didn't get an integer input
                }
            } while (true);

        }
        /*
         System.out.print("Username: ");
         String username = scanner.nextLine();
         System.out.print("Password: ");
         String password = scanner.nextLine();
         System.out.print("First name: ");
         String firstname = scanner.nextLine();
         System.out.print("Lastname: ");
         String lastname = scanner.nextLine();
         System.out.print("Email: ");
         String Email = scanner.nextLine();
         System.out.println(username + " --> " + password);
         */

        for (String str : ret) {
            System.out.println(str);
        }
    }

    private static void login() throws InvalidAuthenticationException {

        Scanner scanner = new Scanner(System.in);
        Console console = System.console();
        final AuthenticateService as = new AuthenticateService();
        

        /*  once migrate to ec2, uncomment the next block
         String username = console.readLine("Username:");
         char[] password = console.readPassword("Password");
        
         System.out.println(new String(password));
         */

        do {
            try {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                as.authenticate(username, password);

                UserSession.getInstance();

            } catch (InvalidAuthenticationException ex) {
                System.err.println(ex.getMessage());
                continue;
            }
        } while (true);

    }

    public static void main(String[] args) throws InvalidAuthenticationException, CreateUserException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("********Welcome to Stockfile********\n");

        int choice = 0;
        do {  // Loop until we have correct input

            System.out.println("Please select one of the following...");
            System.out.println("Login ......... 1");
            System.out.println("New user ...... 2");
            System.out.print(">  ");

            try {
                choice = scanner.nextInt();         // Blocks for user input
                if (choice == 1 || choice == 2) {
                    scanner.nextLine();             // flush
                    break;                          // Got valid input, stop looping
                } else {
                    System.out.println("Please pick between 1 or 2.\n");
                    continue;                       // restart loop, wrong number
                }
            } catch (final InputMismatchException e) {
                System.out.println("Invalid input; please try again.\n");
                scanner.nextLine();                 // discard non-int input
                continue;                           // restart loop, didn't get an integer input
            }
        } while (true);

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                createUser();
                break;
        }
    }
}