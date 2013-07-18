/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.controllers;

import java.io.Console;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import org.joda.time.LocalDate;

import stockfile.security.UserSession;
import stockfile.dao.UserDAO;
import stockfile.exceptions.CreateUserException;
import stockfile.exceptions.CreateUserException.CreateUserError;
import stockfile.exceptions.InvalidAuthenticationException;
import stockfile.models.User;
import stockfile.security.AuthenticateService;
import stockfile.security.RegexHelper;
import stockfile.security.RegexHelper.RegExPattern;

/**
 *
 * @author MrAtheist
 */
public class LoginController {

    private static void createUser() throws CreateUserException, SQLException {

        Scanner scanner = new Scanner(System.in);
        Console console = System.console();
        UserDAO userDAO = new UserDAO();

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

        for (int i = 0; i < arr.length;) {
            
                System.out.print(arr[i] + ": ");

                try {
                    /* if (arr[i].equals("Password"))
                        tmp = new String(console.readPassword(arr[i] + ":"));
                    else tmp = console.readLine(arr[i] + ":");
                    */
                    tmp = scanner.nextLine();         // Blocks for user input
                    if (!RegexHelper.validate(tmp, reg[i]) || tmp.length() == 0) {
                    
                        throw new CreateUserException(err[i]);

                    } else {
                        ret[i] = tmp;
                        i++;                          // Got valid input, stop looping
                    }
                } catch (final CreateUserException e) {
                	
                	System.err.println(e);
                	
                };

            

        }
        
        userDAO.createUser(new User(ret[0], ret[2], ret[3], ret[4], new LocalDate(), System.getProperty("user.home") + "/Stockfile"), ret[1]);
            
    }

    private static void login() throws InvalidAuthenticationException {

        Scanner scanner = new Scanner(System.in);
        Console console = System.console();
        final AuthenticateService as = new AuthenticateService();
        String username, password;
        
        do {
            try {
                
              /*  once migrate to ec2, uncomment the next block
                username = console.readLine("Username:");
                password = new String(console.readPassword("Password"));
        
                System.out.println(new String(password));
                */
                
                System.out.print("Username: ");
                username = scanner.nextLine();
                
                System.out.print("Password: ");
                password = scanner.nextLine();
                
                as.authenticate(username, password);

                break;
            } catch (InvalidAuthenticationException ex) {
                System.err.println(ex.getMessage());
                continue;
            }
        } while (true);

    }

    public static void main(String[] args) throws InvalidAuthenticationException, CreateUserException, SQLException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("********Welcome to Stockfile********\n");

        int choice = 0;
        do {
            
            System.out.println("Please select one of the following...");
            System.out.println("Login .........> 1");
            System.out.println("New user ......> 2");
            System.out.print(">  ");

            try {
                choice = scanner.nextInt();         // Blocks for user input
                if (choice == 1 || choice == 2) {
                    scanner.nextLine();             // flush
                    break;                          // Got valid input, stop looping
                } else {
                    System.err.println("Please pick between 1 or 2.\n");
                    continue;                       // restart loop, wrong number
                }
            } catch (final InputMismatchException e) {
                System.err.println("Invalid input; please try again.\n");
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