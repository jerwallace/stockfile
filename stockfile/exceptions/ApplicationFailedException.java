/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.exceptions;

import com.jcraft.jsch.SftpException;

/**
 *
 * @author Peter Lee, Jeremy Wallace, Bahman Razmpa
 */
@SuppressWarnings("serial")
public class ApplicationFailedException extends SftpException {

    public ApplicationFailedException(String message) {
        super(1,message);
    }
}
