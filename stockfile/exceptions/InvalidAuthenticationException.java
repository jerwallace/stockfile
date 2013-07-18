/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.exceptions;

/**
 *
 * @author 
 */
@SuppressWarnings("serial")
public class InvalidAuthenticationException extends Exception {

    public InvalidAuthenticationException(String message) {

        super(message);
    }
}
