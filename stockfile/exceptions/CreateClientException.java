/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.exceptions;

/**
 *
 * @author Peter Lee, Jeremy Wallace, Bahman Razmpa
 */
@SuppressWarnings("serial")
public class CreateClientException extends Exception {

    String msg = "";
    
    public enum CreateClientError {
    
        TYPE, EMPTY, INVALID_FOLDERPATH;
    }
    
    public CreateClientException(CreateClientError error) {

        switch (error) {
            
            case TYPE:
                
                msg += "Type can not be empty; please try again.\n";
                break;
                
            case INVALID_FOLDERPATH:
                
                msg = msg + "Invalid folder path; please try again.\n";
                break;
            
            case EMPTY:
            default:
                break;
        }
    }
    
    @Override
    public String toString() {
        return msg;
    }
}
