package stockfile.security;

import stockfile.exceptions.InvalidAuthenticationException;

/**
 * Checks if a given string is null or empty
 */
public class AuthenticateValidator {

    protected final String ERROR_MESSAGE = "Invalid Credentials. Please try again.";

    public void validateUserName(String userName) throws InvalidAuthenticationException {

        if (isNullOrEmpty(userName)) 
            throw new InvalidAuthenticationException(ERROR_MESSAGE);
        
    }

    public void validatePassword(String password) throws InvalidAuthenticationException {

        if (isNullOrEmpty(password)) 
            throw new InvalidAuthenticationException(ERROR_MESSAGE);
        
    }

    private boolean isNullOrEmpty(String value) {

        return value == null || value.isEmpty();
    }
}
