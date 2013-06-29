package stockfile.client;

import java.io.Serializable;

/**
 * Class definition for the Singleton User type client session
 */
public class UserSession extends Session implements Serializable
{

    private static UserSession userSession = null;

    protected UserSession()
    {
    }

    //Default constructor for the singleton instance of the UserSession
    public static UserSession getInstance()
    {

        if (userSession == null)
        {

            synchronized (UserSession.class)
            {

                UserSession inst = userSession;

                if (inst == null)
                {

                    synchronized (UserSession.class)
                    {
                        userSession = new UserSession();
                    }
                }
            }
        }

        return userSession;
    }
    /**
     * Public method that returns the Stock_Action value of the currentAction
     * and can be any of the three values" BUY_STOCK, SELL_STOCK, QUERY_STOCK
     * <p/>
     * @return
     */
//    public Stock_Action getCurrentAction()
//    {
//        return this.currentAction;
//    }
    /**
     * Public method that sets the Stock_Action value of the currentAction and
     * can be any of the three values" BUY_STOCK, SELL_STOCK, QUERY_STOCK
     * <p/>
     * @param currentAction the currentAction to set
     */
//    public void setCurrentAction(Stock_Action currentAction)
//    {
//        this.currentAction = currentAction;
//    }
}
