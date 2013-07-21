package stockfile.security;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import stockfile.dao.*;
import stockfile.exceptions.InvalidAuthenticationException;
import stockfile.models.Client;
import stockfile.models.User;

public class AuthenticateService {
    
    protected final String ERROR_MESSAGE = "Invalid Credentials. Please try again.";
    
    private final UserDAO userDao;
    private final ClientDAO clientDAO;
    
    private final AuthenticateValidator authenticateValidator;
    	
    public AuthenticateService() {
        this.userDao = new UserDAO();
        this.clientDAO = new ClientDAO();
        this.authenticateValidator = new AuthenticateValidator();
    }
	
    public void authenticate(String username, String password) throws InvalidAuthenticationException, SocketException, UnknownHostException {

        this.authenticateValidator.validateUserName(username);
        this.authenticateValidator.validatePassword(Security.sha(password));
        
        String macAddr = Client.convertByteArrayString(
                NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress());
        
        System.out.println(username + " " + Security.sha(password));
        User user = this.userDao.getUser(username, Security.sha(password));

        if (user != null) {
            
            if (user.getUserName() == null) 
                throw new InvalidAuthenticationException(ERROR_MESSAGE);
            else {
                UserSession.getInstance().setCurrentUser(user);
                UserSession.getInstance().setCurrentClient(this.clientDAO.getClientByUser(user, macAddr));
                System.out.println("User "+UserSession.getInstance().getCurrentUser().getUserName()+" signed in.");
            }
        } else 
            throw new InvalidAuthenticationException("Could not connect to server.");
    }
}