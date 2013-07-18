package stockfile.dao;

import stockfile.exceptions.CreateUserException;
import stockfile.exceptions.CreateUserException.CreateUserError;
import stockfile.exceptions.InvalidException;
import stockfile.models.User;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.LocalDate;

import static stockfile.dao.StockFileDAO.ps;
import static stockfile.dao.StockFileDAO.rs;
import stockfile.security.Security;

/**
 * This is the DAO class for parsing the resultset and return instance of User.
 *
 * @author MrAtheist
 */
public class UserDAO extends StockFileDAO {

	public UserDAO() {
		super();
	}

	public enum ColumnHeader {

		USERNAME, FIRST_NAME, LAST_NAME, EMAIL, DATE_JOINED, PASSWORD;
	}
	/**
	 * Returns a User object corresponding to the given userName and password
	 *
	 * @param userName
	 * @param password
	 * @return
	 */
	public User getUser(String userName, String password) {

		User user = new User();
                
		try {
			ps = conn.prepareStatement("SELECT * FROM user WHERE username = ? and password = ?");
			ps.setString(1, userName);
			ps.setString(2, password);
			rs = ps.executeQuery();

		} catch (SQLException sqlex) {
			System.err.println("SQLException: " + sqlex.getMessage());
			//sqlex.printStackTrace();
			this.psclose();
                        return null;
		}

		try {
			while (rs.next()) {
				user.setUserName(rs.getString("username"));
				user.setFirstName(rs.getString("first_name"));
				user.setLastName(rs.getString("last_name"));
				user.setEmail(rs.getString("email"));
				user.setDateJoined(new LocalDate(rs.getTimestamp("date_joined")));
			}
		} catch (SQLException sqlex) {
			System.err.println("SQLException: " + sqlex.getMessage());
			sqlex.printStackTrace();
			this.psclose();
			return null;
		}

		this.psclose();
		return user;
	}

	/**
	 * Create a new user in the database with the given User object and password
	 *
	 * @param user
	 * @param password
	 * @return
	 */
	public void createUser(User user, String password) throws CreateUserException, SQLException {

		if (!usernameTaken(user.getUserName())) {

			try {
                            
				ps = conn.prepareStatement("INSERT INTO user"
						+ " (username, first_name, last_name, email, date_joined, password) "
						+ " VALUES (?, ?, ?, ?, ?, ?)");

				ps.setString(1, user.getUserName());
				ps.setString(2, user.getFirstName());
				ps.setString(3, user.getLastName());
				ps.setString(4, user.getEmail());
				ps.setString(5, user.getDateJoined().toString());
				ps.setString(6, Security.sha(password));

				ps.executeUpdate();	
                                
                        } catch (SQLException sqlex) {
				Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, sqlex);
				System.err.println("SQLException: " + sqlex.getMessage());
				sqlex.printStackTrace();
                                this.psclose();
                        }
		} else  
                    throw new CreateUserException(CreateUserError.USERNAME_TAKEN);
                               
                this.psclose();
                
	}

	/**
	 * Checks if the given username already exists in the database
	 *
	 * @param username of a User
	 * @return true if the given username already exists in the database
	 *
	 */
	public boolean usernameTaken(String username) throws SQLException {

		if (username == null) 
			return true;
		
		try {

			ps = conn.prepareStatement("SELECT * FROM user WHERE username = ?");
			ps.setString(1, username);
			rs = ps.executeQuery();

                        if (rs.next()) {
				this.psclose();
				return true;
			} else {
				this.psclose();
				return false;
			}
                        
		} catch (SQLException sqlex) {

			System.err.println("SQLException: " + sqlex.getMessage());
                        this.psclose();
			throw sqlex;
		}
	}

	/**
	 * Returns a User object corresponding to the given attribute and value
	 * @param attribute
	 * @param value
	 * @return 
	 */
	public User getUserByAttribute(ColumnHeader attribute, String value) {

		User user = new User();

		try {
               //       System.out.println("SELECT * FROM user WHERE " + attribute.toString().toLowerCase() + " = ?");
			ps = conn.prepareStatement("SELECT * FROM user WHERE " + attribute.toString().toLowerCase() + " = ?");
			ps.setString(1, value);
			rs = ps.executeQuery();
                        
                        while (rs.next()) {
				user.setUserName(rs.getString("username"));
				user.setFirstName(rs.getString("first_name"));
				user.setLastName(rs.getString("last_name"));
				user.setEmail(rs.getString("email"));
				//user.setDateJoined(new LocalDate(rs.getTimestamp("date_joined")));
			}
                        
		} catch (SQLException sqlex) {
			System.err.println("SQLException: " + sqlex.getMessage());
			
		}
                this.psclose();
		return user;
	}
}
