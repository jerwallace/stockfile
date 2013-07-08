package stockfile.api;

import java.io.Serializable;
import org.joda.time.DateTime;

/**
 * Class describing a User object
 */
public class User implements Serializable {

	private String userName;
	private String first_name;
	private String last_name;
	private String email;
	private DateTime date_joined;
	private String homeDirectory;

	public User() {
	}

	public User(String userName) {
		this.userName = userName;
	}

	public User(String userName, String first_name, String last_name, String email, DateTime date_joined, String homeDir) {

		this.userName = userName;
		this.first_name = first_name;
		this.last_name = last_name;
		this.email = email;
		this.date_joined = date_joined;
		this.homeDirectory = homeDir;
	}
	/**
	 * Public getter returns userName
	 * <p/>
	 * @return - String userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Public setter updates userName
	 * <p/>
	 * @param userName - String userName
	 */
	public void setUserName(String userName) {

		this.userName = userName;
	}

	@Override
	public String toString() {
		return getUserName();
	}

	/**
	 * @return the first_name
	 */
	public String getFirst_name() {
		return first_name;
	}

	/**
	 * @param first_name the first_name to set
	 */
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	/**
	 * @return the last_name
	 */
	public String getLast_name() {
		return last_name;
	}

	/**
	 * @param last_name the last_name to set
	 */
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the homeDirectory
	 */
	public String getHomeDirectory() {
		return homeDirectory;
	}

	/**
	 * @param homeDirectory the homeDirectory to set
	 */
	public void setHomeDirectory(String homeDirectory) {
		this.homeDirectory = homeDirectory;
	}

	/**
	 * @return the date_joined
	 */
	public DateTime getDate_joined() {
		return date_joined;
	}

	/**
	 * @param date_joined the date_joined to set
	 */
	public void setDate_joined(DateTime date_joined) {
		this.date_joined = date_joined;
	}
}