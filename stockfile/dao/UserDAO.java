package stockfile.dao;

import stockfile.api.User;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static stockfile.dao.StockFileDAO.ps;
import static stockfile.dao.StockFileDAO.rs;

/**
 * This is the DAO class for parsing the resultset and return instance of User.
 * @author MrAtheist
 */
public class UserDAO extends StockFileDAO {
    
    public UserDAO() {
        super();
    }

	/**
	 * This method returns a User object corresponding to the given userName and password
	 * @param userName
	 * @param password
	 * @return 
	 */
	public User getUser(String userName, String password) {

		this.initConnection();
		User user = new User();


		return user;
	}

	public int createUser(User user) {

		this.initConnection();

		if (!usernameTaken(user.getUserName())){

			try {
				ps = conn.prepareStatement("INSERT INTO user" 
						+ " (username, first_name, last_name, email, date_joined, password) "
						+ " (VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

			} catch (SQLException ex) {
				Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		}	

		return 1;
	}

    /**
     * This method checks if the given username already exists in the database
     * @param username of a User
     * @return true if the given username already exists in the database
     * 
     */
    public boolean usernameTaken(String username) {
        
        this.initConnection();
        
        if (username==null) return true;
        
        try {

            ps = conn.prepareStatement("SELECT * FROM User WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();

        } catch (SQLException sqlex) {

            System.err.println("SQLException: " + sqlex.getMessage());
            sqlex.printStackTrace();
        }

        // parse the resultset
        try {
            while (rs.next())
                return true;

        } catch (SQLException sqlex) {

            System.err.println("SQLException: " + sqlex.getMessage());
            sqlex.printStackTrace();
            this.psclose();
            return false;
        }   
        this.psclose();
        return false;
    }

}