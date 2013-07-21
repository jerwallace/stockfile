/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.controllers;

import java.awt.Frame;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.LocalDate;

import stockfile.dao.ClientDAO;
import stockfile.dao.UserDAO;
import stockfile.exceptions.ApplicationFailedException;
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
 * Login Controller contains the methods required to authenticate a user, create
 * a new user, and create a new client.
 *
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class LoginController {

    private static LoginController loginController = null;
    private final static AuthenticateService as = new AuthenticateService();
    private static String macAddr;

    /**
     * The constructor initiates the macAddr
     *
     * @throws UnknownHostException
     * @throws SocketException
     */
    private LoginController() throws UnknownHostException, SocketException {

        macAddr = Client.convertByteArrayString(
                NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress());

    }

    /**
     * Static method returns a single
     * @return
     * @throws ApplicationFailedException 
     */
    public static LoginController getInstance() throws ApplicationFailedException {
        if (loginController == null) {
            try {
                loginController = new LoginController();
            } catch (UnknownHostException | SocketException ex) {
                throw new ApplicationFailedException("Mac address could not be determined. Exiting.");
            }
        }
        return loginController;
    }

    /**
     * @return the macAddr
     */
    public static String getMacAddr() {
        return macAddr;
    }

    /**
     * Creates a user and adds it to the user table
     *
     * @param scanner
     * @throws SQLException
     * @throws CreateUserException
     * @throws InvalidAuthenticationException
     * @throws CreateClientException
     * @throws UnknownHostException
     * @throws SocketException
     */
    private void createUser(Scanner scanner) throws SQLException, CreateUserException,
            InvalidAuthenticationException, CreateClientException, UnknownHostException, SocketException {

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

        String[] ret = new String[arr.length];
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

                } else if (arr[i].equals("Username") && userDAO.usernameTaken(tmp)) {
                    throw new CreateUserException(CreateUserError.USERNAME_TAKEN);
                } else {
                    ret[i] = tmp;
                    i++;
                }
            } catch (final CreateUserException e) {
                System.err.println(e);
            }
        }

        User newUser = new User(ret[0], ret[2], ret[3], ret[4], new LocalDate(),
                FilenameUtils.separatorsToSystem(System.getProperty("user.home") + "/Stockfile"));
        userDAO.createUser(newUser, ret[1]);
        UserSession.getInstance().setCurrentUser(newUser);
    }

    /**
     * Creates a client and adds it to the client table along with a link that
     * relates to the user to user_client table
     *
     * @param scanner
     * @throws CreateClientException
     * @throws UnknownHostException
     * @throws SocketException
     * @throws SQLException
     */
    public void createClient(Scanner scanner) throws CreateClientException, UnknownHostException, SocketException, SQLException, UnsupportedEncodingException {

        ClientDAO clientDAO = new ClientDAO();
        Client newClient = new Client();

        String ipAddr = InetAddress.getLocalHost().getHostAddress();
        String homeDir = FilenameUtils.separatorsToSystem(System.getProperty("user.home") + "/");

        String[] arr = {"Type", "Description", "Manufacturer", "Model Number", "Home Directory"};

        CreateClientError[] err = {CreateClientException.CreateClientError.TYPE,
            CreateClientException.CreateClientError.EMPTY,
            CreateClientException.CreateClientError.EMPTY,
            CreateClientException.CreateClientError.EMPTY,
            CreateClientException.CreateClientError.INVALID_FOLDERPATH};

        RegExPattern[] reg = {RegexHelper.RegExPattern.TYPE,
            RegexHelper.RegExPattern.TEXT,
            RegexHelper.RegExPattern.TEXT,
            RegexHelper.RegExPattern.TEXT,
            RegexHelper.RegExPattern.FOLDERPATH};

        String[] ret = new String[arr.length];
        String tmp;

        System.out.println("Please supply the following information for your client.");

        for (int i = 0; i < arr.length;) {

            System.out.print(arr[i] + ": ");

            try {
                if (arr[i].equals("Home Directory")) {
                    System.out.print(homeDir);
                }

                tmp = scanner.nextLine();

                if (arr[i].equals("Type") && clientDAO.typeExists(tmp)) {
                    newClient = clientDAO.getClientByType(tmp);
                    i = arr.length - 1;

                } else if (!RegexHelper.validate(tmp, reg[i])) {

                    throw new CreateClientException(err[i]);

                } else {
                    ret[i] = tmp;
                    i++;
                }

            } catch (final CreateClientException e) {

                System.err.println(e);
            };
        }

        File dir = new File(homeDir + ret[4]);
        if (!dir.exists()) {
            System.out.println("The specified directory does not exist. System will now create it...");
            if (!dir.mkdirs()) {
                System.err.println("The specified directory could not be created.");
            }
        }

        if (newClient.getType() == null) {
            newClient = new Client(ret[0], ret[1], ret[2], ret[3], ret[4]);
            clientDAO.addClient(newClient);
        }

        if (clientDAO.getClientByUser(UserSession.getInstance().getCurrentUser(), macAddr) == null) {
            // no association found between the user's mac address and client type
            // add an entry to the user_client table

            newClient = new Client(newClient.getType(),
                    newClient.getDescription(),
                    newClient.getManufacturer(),
                    newClient.getModelNo(),
                    ret[4], ipAddr, macAddr);
            clientDAO.addUserClient(newClient);

        }

        UserSession.getInstance().setCurrentClient(newClient);
    }

    /**
     * Prompts a user for login credentials and populates UserSession object
     * accordingly
     *
     * @param scanner
     */
    private void login(Scanner scanner) {

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

            } catch (Exception e) {
                System.err.println(e);
                continue;
            }

        } while (true);

    }

    public void run() {

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
                login(scanner);
                break;
            case 2:
                try {
                    createUser(scanner);
                    createClient(scanner);
                } catch (CreateUserException e) {
                    System.err.println(e);
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
            frame.setExtendedState(Frame.ICONIFIED);
            frame.setExtendedState(Frame.NORMAL);

        }
    }
}
