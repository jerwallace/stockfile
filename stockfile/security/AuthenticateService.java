package stockfile.security;

import stockfile.api.User;
import stockfile.client.UserSession;
import stockfile.dao.*;

public class AuthenticateService {
    
    protected final String ERROR_MESSAGE = "Invalid Credentials. Please try again.";
    
    private final UserDAO userDao;
    private UserSession currentUserSession = UserSession.getInstance();
    
    private final AuthenticateValidator authenticateValidator;
    	
    public AuthenticateService() {
        this.userDao = new UserDAO();
        this.authenticateValidator = new AuthenticateValidator();
    }
	
    public void authenticate(String username, String password) throws InvalidAuthenticationException {

        this.authenticateValidator.validateUserName(username);
        this.authenticateValidator.validatePassword(Security.sha(password));
        
        System.out.println(username + " " + Security.sha(password));
        User user = this.userDao.getUser(username, Security.sha(password));
        
        if (user!=null) {
            if (user.getUserName() == null) 
                throw new InvalidAuthenticationException(ERROR_MESSAGE);
            else
                currentUserSession.setCurrentUser(user);
            
        } else 
            throw new InvalidAuthenticationException("Could not connect to server.");
    }
}