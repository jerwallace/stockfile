
package stockfile.dao;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;

import stockfile.models.Client;
import stockfile.models.User;
import stockfile.security.StockFileSession;

/**
 * Client DAO
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class ClientDAO extends StockFileDAO {

    public ClientDAO() {
        super(true);
    }

    /**
     * Create a new user client in the database with the given Client object
     *  and the username of the current user within UserSession
     * @param client
     * @throws SQLException
     * @throws SocketException
     * @throws UnknownHostException
     * @throws UnsupportedEncodingException 
     */
    public void addUserClient(Client client) throws SQLException, SocketException, UnknownHostException, UnsupportedEncodingException {

        try {

            ps = conn.prepareStatement("INSERT INTO "
                    + "user_client (username,client_type,last_sync,ip_address,mac_address,home_directory) "
                    + "VALUES (?,?,?,?,?,?);");

            ps.setString(1, StockFileSession.getInstance().getCurrentUser().getUserName());
            ps.setString(2, client.getType());
            ps.setTimestamp(3, StockFileSession.getInstance().getLastSync());
            ps.setString(4, client.getIpAddress());
            ps.setString(5, client.getMacAddress());
            ps.setString(6, client.getHomeDir());

            ps.executeUpdate();
            
        } catch (SQLException sqlex) {
            throw sqlex;
        }

        this.psclose();
    }

    /**
     * Removes the entry from the user client table that matches the given Client object
     *  and the username in the UserSession
     * @param client
     * @throws SQLException 
     */
    public void removeUserClient(Client client) throws SQLException {
     
        try {
            ps = conn.prepareStatement("DELETE FROM user_client WHERE username = ? AND mac_address = ?");

            ps.setString(1, StockFileSession.getInstance().getCurrentUser().getUserName());
            ps.setString(2, client.getMacAddress());

            ps.executeUpdate();
        } catch (SQLException sqlex) {
            throw sqlex;
        }

        this.psclose();
    }

    /**
     * Updates the entry in the user client table that corresponds to the given 
     *  Client object and the username in the UserSession
     * @param client
     * @throws SQLException 
     */
    public void updateUserClient(Client client) throws SQLException {
     
        try {
            ps = conn.prepareStatement("UPDATE user_client "
                    + "SET client_type = ?, last_sync = ?, ip_address = ?, home_directory = ?"
                    + "WHERE username = ? AND mac_address = ? ");

            ps.setString(1, client.getType());
            ps.setTimestamp(2, StockFileSession.getInstance().getLastSync());
            ps.setString(3, client.getIpAddress());
            ps.setString(4, client.getHomeDir());
            ps.setString(5, StockFileSession.getInstance().getCurrentUser().getUserName());
            ps.setString(6, client.getMacAddress());

            ps.executeUpdate();

        } catch (SQLException sqlex) {
            throw sqlex;
        }
        this.psclose();
    }

    /**
     * Creates a Client in the client table based on the Client object provided
     * @param client
     * @throws SQLException
     * @throws SocketException
     * @throws UnknownHostException 
     */
    public void addClient(Client client) throws SQLException, SocketException, UnknownHostException {
    
        try {

            ps = conn.prepareStatement("INSERT INTO "
                    + "client (client_type, client_description, client_manufacturer, client_model_no) "
                    + "VALUES (?,?,?,?);");

            ps.setString(1, client.getType());
            ps.setString(2, client.getDescription());
            ps.setString(3, client.getManufacturer());
            ps.setString(4, client.getModelNo());

            ps.executeUpdate();

        } catch (SQLException sqlex) {
            throw sqlex;
        }

        this.psclose();
    }

    /**
     * Removes the entry in the client table that corresponds to the client type
     *  of the Client object provided
     * @param client
     * @throws SQLException 
     */
    public void removeClient(Client client) throws SQLException {
    
        try {
            ps = conn.prepareStatement("DELETE FROM client WHERE client_type = ?");

            ps.setString(1, client.getType());

            ps.executeUpdate();
            
        } catch (SQLException sqlex) {
            throw sqlex;
        }

        this.psclose();
    }

    /**
     * Updates the entry in the client table based on the Client object provided
     * @param client
     * @throws SQLException 
     */
    public void updateClient(Client client) throws SQLException {
    
        try {
            ps = conn.prepareStatement("UPDATE client "
                    + "SET client_desc = ?, client_manufacturer = ?, client_model_no = ? "
                    + "WHERE client_type = ?");

            ps.setString(1, client.getDescription());
            ps.setString(2, client.getManufacturer());
            ps.setString(3, client.getModelNo());
            ps.setString(4, client.getType());

            ps.executeUpdate();

        } catch (SQLException sqlex) {
            throw sqlex;
        }
        this.psclose();
    }

    /**
     * Returns a Client object that matches to the given type of client
     * @param type
     * @return
     * @throws SQLException 
     */
    public Client getClientByType(String type) throws SQLException {

        Client client = new Client();

        try {
            ps = conn.prepareStatement("SELECT * FROM client where client_type = ?");
            ps.setString(1, type);
            rs = ps.executeQuery();

            while (rs.next()) {
                client.setType(rs.getString("client_type"));
                client.setDescription(rs.getString("client_description"));
                client.setManufacturer(rs.getString("client_manufacturer"));
                client.setModelNo(rs.getString("client_model_no"));
            }
        } catch (SQLException sqlex) {
            System.err.println("SQLException: " + sqlex.getMessage());
        }
        this.psclose();
        return client;
    }

    /**
     * Returns a unique Client object based on the User object and the MAC
     *  address provided
     * @param user
     * @param mac
     * @return 
     */
    public Client getClientByUser(User user, String mac) {
   
        boolean hasClient = false;
        Client client = new Client();

        try {

            ps = conn.prepareStatement("SELECT * FROM client c, user_client uc "
                    + "WHERE uc.username = ? AND uc.mac_address = ? AND c.client_type = uc.client_type");

            ps.setString(1, user.getUserName());
            ps.setString(2, mac);
            rs = ps.executeQuery();

            while (rs.next()) {
                hasClient = true;
                client.setType(rs.getString("client_type"));
                client.setDescription(rs.getString("client_description"));
                client.setManufacturer(rs.getString("client_manufacturer"));
                client.setModelNo(rs.getString("client_model_no"));
                client.setHomeDir(rs.getString("home_directory"));
                client.setIpAddress(rs.getString("ip_address"));
                client.setMacAddress(rs.getString("mac_address"));
            }
            
            if (!hasClient)
                return null;

        } catch (SQLException sqlex) {
            System.err.println("SQLException: " + sqlex.getMessage());
            this.psclose();
        }

        this.psclose();
        return client;
    }

    /**
     * Returns a hashmap of <macAddr, Client> that the given User object is 
     *  associated to
     * @param user
     * @return
     * @throws Exception 
     */
    public HashMap<String, Client> getClientsByUser(User user) throws Exception {

        HashMap<String, Client> clientHashMap = new HashMap<>();

        try {
            ps = conn.prepareStatement("SELECT * FROM user_client JOIN client ON user_client.client_type=client.client_type WHERE username = ?");

            ps.executeUpdate();

            while (rs.next()) {
                String clientType = rs.getString("client_type");
                String clientDesc = rs.getString("client_description");
                String clientManuf = rs.getString("client_manufacturer");
                String clientModelNo = rs.getString("client_model_no");
                String clientHomeDir = rs.getString("home_directory");
                String clientIpAddress = rs.getString("ip_address");
                String clientMacAddress = rs.getString("mac_address");
                Client newClient = new Client(clientType, clientDesc, clientManuf, clientModelNo, clientHomeDir, clientIpAddress, clientMacAddress);
                clientHashMap.put(clientMacAddress, newClient);
            }
        } catch (Exception sqlex) {
            throw sqlex;
        }
        this.psclose();

        return clientHashMap;
    }

    /**
     * Checks if the given type already exists in the client table
     *
     * @param type
     * @return true if the given type already exists in the "client" table
     * @throws SQLException
     */
    public boolean typeExists(String type) throws SQLException {

        if (type.length() == 0)
            return false;

        try {

            ps = conn.prepareStatement("SELECT * from client WHERE client_type = ?");
            ps.setString(1, type);
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
}
