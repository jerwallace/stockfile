package stockfile.client.protocol;

import java.rmi.RemoteException;
import stockfile.api.User;
import stockfile.api.UserApi;
import stockfile.client.UserSession;

/**
 * User Protocol contains all of the actions and the flow of states between the
 * server and the client.
 */
public class UserProtocol extends AbstractProtocol
{

    // This is the menu screen first displayed when the user is logged in.
    //private String menu = "1. Buy Stock    2. Sell Stock   3. Query Stock  4. Print Stock  5. Logout";
    UserSession thisSession = UserSession.getInstance();

    /**
     * getInstruction will return a question to the client based on a state.
     * <p/>
     * @param currentState The current state the user is in.
     * <p/>
     * @return The corresponding instruction.
     */
    @Override
    public String getInstruction() throws CustomException
    {
        switch (thisSession.getInstance().getCurrentState())
        {
            case LOGIN:
                return "Login:";
            default:
                return "";
        }
    }

    /**
     * processInput accepts input from the user and performs an action based on
     * the input and the users current state and then sends a response from the
     * server.
     * <p/>
     * @param input The user response.
     * <p/>
     * @return The server response.
     * <p/>
     * @throws RemoteException
     */
    @Override
    public String processInput(String input) throws CustomException, RemoteException
    {
        User currentUser = thisSession.getRemoteApi().getUser(thisSession.getUsername());
        String output = "";

        if (input.equalsIgnoreCase("cancel"))
        {
            thisSession.setCurrentState(AbstractProtocol.State.SELECT_COMMAND);
            return null;
        }
        else
        {
            switch (thisSession.getCurrentState())
            {
                case LOGIN:
                    thisSession.setCurrentState(AbstractProtocol.State.SELECT_COMMAND);
                    thisSession.setUsername(input);
                    String action = "";

                    // If the user does not exist, a new user will be created.
                    if (thisSession.getRemoteApi().userExists(thisSession.getUsername()))
                    {
                        action = "signed in";
                    }
                    else
                    {
                        action = "created";
                    }

                    currentUser = thisSession.getRemoteApi().getUser(thisSession.getUsername());
                    return "User " + thisSession.getUsername();
                default:
                    return "";
            }

        }

    }

    
}
