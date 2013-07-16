package sandbox.heartbeat;

public class Users
{// class for holding the structure for the username information

    public Users(String u, String add, String ipstr, int p)
    {// constructor , pass paremeters

        username = u;
        addr = add;
        ip = ipstr;
        port = p;
        hb = 0;

    }
    private String username;
    private String addr;
    private int port;
    private String ip;
    private int hb;

    public boolean equals(String name, String ipadr, int pp)
    {
        // check for equals if not hashed

        return (username.equals(name) && ip.equals(ipadr) && (port == pp));
    }

    public String getUsername()
    {
        return username;
    }

    public String getKey()
    {
        return ip + port;
    }

    public String toString()
    { // returns an output string username@addr/port
        return (username + "@" + addr + "/" + port);
    }

    public String toToken()
    { //returns a tokenized version of the list
        return (username + "|" + addr + "|" + ip + "|" + port + "|");
    }

    public String getShortToken()
    { //returns a short tokenized version of the user
        return (ip + "|" + port + "|");
    }

    public synchronized void resetHB()
    { // sets the HB equal to zero
        hb = 0;
    }

    public synchronized int incHB()
    { //adds to the hb
        hb++;
        return hb;
    }

    public synchronized int getHB()
    // returns the hb value
    {
        return hb;
    }
}
