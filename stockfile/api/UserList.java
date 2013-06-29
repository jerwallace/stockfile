package stockfile.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Class describing the Singleton UserList
 */
public class UserList
{

    //Map containing "key=userName" &  "value=User Object"
    private Map<String, User> userList;
    private static UserList currentUserList;

    /**
     * Singleton class constructor
     */
    protected UserList()
    {
        this.userList = new HashMap<>();
    }

    /**
     * Returns the only userList instance object
     */
    public static UserList getInstance()
    {

        if (currentUserList == null)
        {
            synchronized (UserList.class)
            {

                UserList inst = currentUserList;

                if (inst == null)
                {

                    synchronized (UserList.class)
                    {
                        currentUserList = new UserList();
                    }
                }
            }
        }

        return currentUserList;
    }

    /**
     *
     * @param username <p/>
     * @return
     */
    public User getUser(String username)
    {
        return userList.get(username);
    }

    /**
     * Public method that adds User object to userList using their userName
     * <p/>
     * @param userName <p/>
     * @return - User object
     */
    public User addUser(String userName)
    {
        User newUser = new User(userName);
        userList.put(userName, newUser);
        return newUser;
    }

    /**
     * Public method that removes a User object referenced by userName from the
     * userList
     * <p/>
     * @param userName
     */
    public void removeUser(String userName)
    {
        userList.remove(userName);
    }

    public void importUserList(Map<String, User> recoveredUserList)
    {
        this.userList = recoveredUserList;
    }

    public Map<String, User> exportUserList()
    {
        return this.userList;
    }

    @Override
    public String toString()
    {
        String mapString = "";
        mapString = "Username \n";
        for (Map.Entry<String, User> entry : this.userList.entrySet())
        {
            mapString += entry.getValue() + "\n";
        }
        return mapString;
    }
}
