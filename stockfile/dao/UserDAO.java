package stockfile.dao;

import stockfile.api.User;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import static stockfile.dao.StockFileDAO.ps;
import static stockfile.dao.StockFileDAO.rs;

/**
 * This is the DAO class for parsing the resultset and return instance of User.
 *
 * @author MrAtheist
 */
public class UserDAO extends StockFileDAO {

	public UserDAO() {
		super();
	}

	/**
	 * Returns a User object corresponding to the given userName and password
	 *
	 * @param userName
	 * @param password
	 * @return
	 */
	public User getUser(String userName, String password) {

		this.initConnection();
		User user = new User();

		try {
			ps = conn.prepareStatement("SELECT * FROM user WHERE username = ? and password = ?");
			ps.setString(1, userName);
			ps.setString(2, password);
			rs = ps.executeQuery();

		} catch (SQLException sqlex) {
			System.err.println("SQLException: " + sqlex.getMessage());
			//sqlex.printStackTrace();
			return null;
		}

		try {

			while (rs.next()) {
				user.setUserName(rs.getString("username"));
				user.setFirst_name(rs.getString("first_name"));
				user.setLast_name(rs.getString("last_name"));
				user.setEmail(rs.getString("email"));
				user.setDate_joined(new DateTime(rs.getTimestamp("date_joined")));
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
	public int createUser(User user, String password) {

		this.initConnection();

		if (!usernameTaken(user.getUserName())) {

			try {
				ps = conn.prepareStatement("INSERT INTO user"
						+ " (username, first_name, last_name, email, date_joined, password) "
						+ " (VALUES (?, ?, ?, ?, NOW(), ?)", Statement.RETURN_GENERATED_KEYS);

				ps.setString(1, user.getUserName());
				ps.setString(2, user.getFirst_name());
				ps.setString(3, user.getLast_name());
				ps.setString(4, user.getEmail());
				ps.setString(6, password);

				ps.executeUpdate();
				conn.commit();
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
//					return rs.getInt(1);
					return 1;
				} else {
					throw new SQLException("Creating user failed, no generated key obtained.");
				}
			} catch (SQLException sqlex) {
				Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, sqlex);
				System.err.println("SQLException: " + sqlex.getMessage());
				sqlex.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * Checks if the given username already exists in the database
	 *
	 * @param username of a User
	 * @return true if the given username already exists in the database
	 *
	 */
	public boolean usernameTaken(String username) {

		this.initConnection();

		if (username == null) {
			return true;
		}

		try {

			ps = conn.prepareStatement("SELECT * FROM user WHERE username = ?");
			ps.setString(1, username);
			rs = ps.executeQuery();

		} catch (SQLException sqlex) {

			System.err.println("SQLException: " + sqlex.getMessage());
			sqlex.printStackTrace();
		}

		// parse the resultset
		try {
			while (rs.next()) {
				return true;
			}

		} catch (SQLException sqlex) {

			System.err.println("SQLException: " + sqlex.getMessage());
			sqlex.printStackTrace();
			this.psclose();
			return false;
		}
		this.psclose();
		return false;
	}

	/**
	 * Returns a User object corresponding to the given attribute and value
	 * @param attribute
	 * @param value
	 * @return 
	 */
	public User getUserByAttribute(String attribute, String value) {

		this.initConnection();
		User user = new User();

		try {
			ps = conn.prepareStatement("SELECT * FROM user WHERE ? = ?");
			ps.setString(1, attribute);
			ps.setString(2, value);
			rs = ps.executeQuery();

		} catch (SQLException sqlex) {
			System.err.println("SQLException: " + sqlex.getMessage());
			//sqlex.printStackTrace();
			return null;
		}

		try {

			while (rs.next()) {
				user.setUserName(rs.getString("username"));
				user.setFirst_name(rs.getString("first_name"));
				user.setLast_name(rs.getString("last_name"));
				user.setEmail(rs.getString("email"));
				user.setDate_joined(new DateTime(rs.getTimestamp("date_joined")));
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
}