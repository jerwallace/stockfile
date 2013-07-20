/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.controllers;

import java.awt.Graphics;
import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.joda.time.LocalDate;
import stockfile.controllers.LoginController.ChooseFile;
import stockfile.dao.ClientDAO;

import stockfile.dao.UserDAO;
import stockfile.exceptions.CreateClientException;
import stockfile.exceptions.CreateClientException.CreateClientError;
import stockfile.exceptions.CreateUserException;
import stockfile.exceptions.CreateUserException.CreateUserError;
import stockfile.exceptions.InvalidAuthenticationException;
import stockfile.models.Client;
import stockfile.models.User;
import stockfile.security.AuthenticateService;
import stockfile.security.RegexHelper;
import stockfile.security.RegexHelper.RegExPattern;
import stockfile.security.UserSession;

/**
 *
 * @author MrAtheist
 */
public class LoginController {

    private final static AuthenticateService as = new AuthenticateService();

    private static void createUser() throws SQLException, CreateUserException, 
                InvalidAuthenticationException, CreateClientException, UnknownHostException, SocketException {

        Scanner scanner = new Scanner(System.in);
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
                tmp = scanner.nextLine();
                if (!RegexHelper.validate(tmp, reg[i]) || tmp.length() == 0) {

                    throw new CreateUserException(err[i]);

                } else {
                    ret[i] = tmp;
                    i++;
                }
            } catch (final CreateUserException e) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, e);
                System.err.println(e);

            };
        }
        scanner.close();
        User newUser = new User(ret[0], ret[2], ret[3], ret[4], new LocalDate(), System.getProperty("user.home") + "/Stockfile");
        userDAO.createUser(newUser, ret[1]);
        UserSession.getInstance().setCurrentUser(newUser);

    }

    private static void createClient() throws CreateClientException, UnknownHostException, SocketException, SQLException {

        Scanner scanner = new Scanner(System.in);
        ClientDAO clientDAO = new ClientDAO();

        String[] arr = {"Type", "Description", "Manufacturer", "Model Number", "Home Directory"};

        CreateClientError[] err = {CreateClientException.CreateClientError.EMPTY,
            CreateClientException.CreateClientError.EMPTY,
            CreateClientException.CreateClientError.EMPTY,
            CreateClientException.CreateClientError.EMPTY,
            CreateClientException.CreateClientError.INVALID_FOLDERPATH};

        RegExPattern[] reg = {RegexHelper.RegExPattern.TEXT,
            RegexHelper.RegExPattern.TEXT,
            RegexHelper.RegExPattern.TEXT,
            RegexHelper.RegExPattern.TEXT,
            RegexHelper.RegExPattern.FOLDERPATH};

        String homeDir = System.getProperty("user.home") + (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0 ? "\\" : "/");
        String[] ret = new String[5];
        String tmp;

        System.out.println("Please supply the following information for your client.");

        for (int i = 0; i < arr.length;) {

            System.out.print(arr[i] + ": ");

            try {
                if (arr[i].equals("Home Directory"))
                    System.out.print(homeDir);
 
                tmp = scanner.next();
                if (!RegexHelper.validate(tmp, reg[i])) {
                    
                    throw new CreateClientException(err[i]);
                } else {
                    ret[i] = tmp;
                    i++;
                }
                
            } catch (final CreateClientException e) {

                System.err.println(e);
            };
        }
        scanner.close();

        File dir = new File(homeDir + ret[4]);
        if (!dir.exists()) {
            System.out.println("The specified directory does not exist. System will now create it.");
            dir.mkdirs();
        }
        
        Client newClient = new Client(ret[0], ret[1], ret[2], ret[3]);
        clientDAO.addClient(newClient);
        clientDAO.addUserClient(newClient);
        
    //    String user = UserSession.getInstance().getCurrentUser().getUserName();

    }

    private static void login() {

        Scanner scanner = new Scanner(System.in);

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
        scanner.close();

    }

    public static void run() {

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
                try {
                    createUser();
                    createClient();
                } catch (CreateUserException ex) {
                    System.err.println(ex.getMessage());
                } catch (Exception e) {
                    System.err.println(e.getStackTrace());
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, e);
                }
                break;
        }

        scanner.close();

    }

    class ChooseFile {

        private JFrame frame;

        public ChooseFile() {
            frame = new JFrame();

            frame.setVisible(true);
            BringToFront();
        }

        public File getFile() {
            JFileChooser fc = new JFileChooser();
            if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null)) {
                frame.setVisible(false);
                return fc.getSelectedFile();
            } else {
                System.out.println("Next time select a file.");
                System.exit(1);
            }
            return null;
        }

        private void BringToFront() {
            frame.setExtendedState(JFrame.ICONIFIED);
            frame.setExtendedState(JFrame.NORMAL);

        }
    }
}
