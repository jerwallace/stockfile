package stockfile.api;

import java.io.Serializable;
import org.joda.time.LocalDate;

/**
 * Class describing a User object
 */
public class User implements Serializable {

	private String userName;
	private String firstName;
	private String lastName;
	private String email;
	private LocalDate dateJoined;
	private String homeDirectory;

	public User() {
	}

	public User(String userName) {
		this.userName = userName;
	}

	public User(String userName, String firstName, String lastName, String email, LocalDate dateJoined, String homeDir) {

		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.dateJoined = dateJoined;
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
		return "Username: " + getUserName() +
			   "\nFirst name: " + getFirstName() +
			   "\nLast name: " + getLastName() +
			   "\nEmail: " + getEmail();
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	 * @return the dateJoined
	 */
	public LocalDate getDateJoined() {
		return dateJoined;
	}

	/**
	 * @param dateJoined the dateJoined to set
	 */
	public void setDateJoined(LocalDate dateJoined) {
		this.dateJoined = dateJoined;
	}
}