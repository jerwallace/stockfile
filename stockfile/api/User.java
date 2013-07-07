package stockfile.api;

import java.io.Serializable;
import org.joda.time.DateTime;

/**
 * Class describing a User object
 */
public class User implements Serializable
{

    private String userName;
	private String first_name;
	private String last_name;
	private String email;
	private DateTime date_joined;
    private String homeDirectory;

	public User() {
		this.userName = "";
	}
    /**
     * Default public constructor for the User class
     * <p/>
     * @param uName - String User Name
     */
    public User(String uName)
    {
        setUserName(uName);
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
        return getUserName();
    }
}
