/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.security;

/**
 *
 * @author 
 */
public class InvalidAuthenticationException extends Exception {

    public InvalidAuthenticationException(String message) {

        super(message);
    }
}
