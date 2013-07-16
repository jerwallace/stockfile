package stockfile.models;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;

import org.joda.time.LocalDate;

import stockfile.dao.ClientDAO;

/**
 * Class describing a User object
 */
public class User implements Serializable
{

    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateJoined;
    private String homeDirectory;
    private HashMap<byte[], Client> userClients = new HashMap<>();
    private ClientDAO clientDao = new ClientDAO();

    public User()
    {
    }

    public User(String userName)
    {
        this.userName = userName;
    }

    public User(String userName, String firstName, String lastName, String email, LocalDate dateJoined, String homeDir)
    {

        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateJoined = dateJoined;
        this.homeDirectory = homeDir;
        try
        {
            this.userClients = clientDao.getClientsByUser(this);
        }
        catch (Exception sqlx)
        {
            System.err.println("Error Finding Client List for this User");
        }
    }

    public boolean userClientExists(Client client)
    {
        return userClients.containsKey(client.getMacAddress());
    }

    /**
     * Public getter returns userName
     * <p/>
     * @return - String userName
     */
    public String getUserName()
    {
        return this.userName;
    }

    /**
     * Public setter updates userName
     * <p/>
     * @param userName - String userName
     */
    public void setUserName(String userName)
    {

        this.userName = userName;
    }

    @Override
    public String toString()
    {
        return "Username: " + getUserName()
                + "\nFirst name: " + getFirstName()
                + "\nLast name: " + getLastName()
                + "\nEmail: " + getEmail();
    }

    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * @return the homeDirectory
     */
    public String getHomeDirectory()
    {
        return homeDirectory;
    }

    /**
     * @param homeDirectory the homeDirectory to set
     */
    public void setHomeDirectory(String homeDirectory)
    {
        this.homeDirectory = homeDirectory;
    }

    /**
     * @return the dateJoined
     */
    public LocalDate getDateJoined()
    {
        return dateJoined;
    }

    /**
     * @param dateJoined the dateJoined to set
     */
    public void setDateJoined(LocalDate dateJoined)
    {
        this.dateJoined = dateJoined;
    }

    public HashMap<byte[], Client> getUserClients()
    {
        return userClients;
    }

    public ClientDAO getClientDao()
    {
        return clientDao;
    }

    public void setUserClients(HashMap<byte[], Client> userClients)
    {
        this.userClients = userClients;
    }

    public void setClientDao(ClientDAO clientDao)
    {
        this.clientDao = clientDao;
    }
}
