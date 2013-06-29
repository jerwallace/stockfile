/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.client.protocol;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author WallaceJ
 */
public abstract class AbstractProtocol
{

    public enum State
    {

        LOGIN, SELECT_COMMAND
    }
    protected ArrayList<String> messages = new ArrayList<String>();

    public abstract String getInstruction() throws CustomException;

    public abstract String processInput(String input) throws CustomException, RemoteException;
}
