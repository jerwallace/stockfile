package stockfile.security;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Scanner;
import stockfile.controllers.LoginController;
import stockfile.dao.*;
import stockfile.exceptions.ApplicationFailedException;
import stockfile.exceptions.CreateClientException;
import stockfile.exceptions.InvalidAuthenticationException;
import stockfile.models.Client;
import stockfile.models.User;

public class Authenicate {

    protected final String ERROR_MESSAGE = "Invalid Credentials. Please try again.";
    private final UserDAO userDao;
    private final ClientDAO clientDAO;
    private final AuthenticateValidator authenticateValidator;

    /**
     * Default constructor
     */
    public Authenicate() {
        this.userDao = new UserDAO();
        this.clientDAO = new ClientDAO();
        this.authenticateValidator = new AuthenticateValidator();
    }

    /**
     * Authenticates the user with the provided username and password
     * 
     * @param username
     * @param password
     * @throws InvalidAuthenticationException
     * @throws SocketException
     * @throws UnknownHostException
     * @throws ApplicationFailedException
     * @throws CreateClientException
     * @throws SQLException
     * @throws UnsupportedEncodingException 
     */
    public void authenticate(String username, String password) throws
            InvalidAuthenticationException, SocketException, UnknownHostException, ApplicationFailedException, CreateClientException, SQLException, UnsupportedEncodingException {

        this.authenticateValidator.validateUserName(username);
        this.authenticateValidator.validatePassword(Security.sha(password));

//        System.out.println(username + " " + Security.sha(password));
        User user = this.userDao.getUser(username, Security.sha(password));

        if (user != null) {

            if (user.getUserName() == null) {
                throw new InvalidAuthenticationException(ERROR_MESSAGE);
            } else {
                StockFileSession.getInstance().setCurrentUser(user);
                System.out.println("User " + StockFileSession.getInstance().getCurrentUser().getUserName() + " signed in.");

                // attach client
                Client client = this.clientDAO.getClientByUser(user, LoginController.getMacAddr());
          
                if (client != null) {
                    StockFileSession.getInstance().setCurrentClient(client);
                    System.out.println("Attaching client: " + StockFileSession.getInstance().getCurrentClient().getType());
                } else {
                    LoginController.getInstance().createClient(new Scanner(System.in));
                }
            }
        } else {
            throw new InvalidAuthenticationException("Could not connect to server.");
        }
    }
}