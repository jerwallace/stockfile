package sandbox.heartbeat;

import java.util.*;
import java.util.HashMap;

public class Players
{// This class maintains a hash table of all players in chat
// also serves as a signaling function for updates

    // list of all Users active
    private HashMap<String, Users> p;

    public Players()
    {
        // the constructor sets up the storage for the info we need
        this.p = new HashMap();
    }

//    public synchronized void joinList(String userName, String address, String ip, int port)
//    {// add to the  list
//
//        String fullAddress = ip + String.valueOf(port);
//        p.put(fullAddress, new Users(userName, address, ip, port));
//    }
    public synchronized void addUser(Users user)
    {
        this.p.put(user.getUsername(), user);
    }

    public synchronized String findUser(String ip, int port)
    {// returns the username with this ip and port

        String fullAddress = ip + String.valueOf(port);

        Users obj = p.get(fullAddress);

        if (obj == null)
        {
            return null;
        }
        return obj.getUsername();
    }

    public String findUserData(String ip, int port)
    {// returns the user string with this ip and port
        String fullAddress = ip + String.valueOf(port);

        Users obj = p.get(fullAddress);

        if (obj == null)
        {
            return null;
        }
        return obj.toString();

    }

    public String getUserToken(String ip, int port)
    { // returns the token of the user

        String fullAddress = ip + String.valueOf(port);

        Users obj = p.get(fullAddress);

        if (obj == null)
        {
            return null;
        }
        return obj.getShortToken();
    }

    public synchronized String checkHB()
    { // traverse list to find 1st users with bad hb >5

        for (Map.Entry<String, Users> entry : p.entrySet())
        {
            Users u = entry.getValue();

            if (u.getHB() > 10)
            {
                // this hb is too large so delete and return
                String key = u.getKey();
                p.remove(key);
                return u.getShortToken();
            }
        }

        // fall through all good
        return null;
    }

    public synchronized void incHB()
    { // increment the hb of each user

        for (Map.Entry<String, Users> entry : p.entrySet())
        {
            Users u = entry.getValue();
            u.incHB();
        }
    }

    public synchronized String getToken()
    {// return a tokenized string of all Userss
        String token = "";
        for (Map.Entry<String, Users> entry : p.entrySet())
        {
            Users u = entry.getValue();
            token += u.toToken();
        }
        return token;
    }

    public synchronized void leaveList(String ip, int port)
    {// removes the Users i from the list
        String fullAddress = ip + String.valueOf(port);
        p.remove(fullAddress);
    }

    public synchronized void resetHB(String ip, int port)
    {//reset the hb of this user
        String fullAddress = ip + String.valueOf(port);
        p.get(fullAddress).resetHB();

    }

    public synchronized int countUsers()
    {
        return p.size();
    }
}
