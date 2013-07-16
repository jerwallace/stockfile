/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;

import stockfile.client.UserSession;
import stockfile.models.Client;
import stockfile.models.User;

/**
 *
 * @author MrAtheist
 */
public class ClientDAO extends StockFileDAO
{

    public ClientDAO()
    {
        super();
    }

    public void addUserClient(Client client) throws SQLException, SocketException, UnknownHostException
    {

//        System.out.println("IP Address: " + ipAddress.getAddress());
//        System.out.println("MAC Address: " + macAddress);

        try
        {

            ps = conn.prepareStatement("INSERT INTO "
                    + "user_client (username,client_type,last_sync,ip_address,mac_address) "
                    + "VALUES (?,?,?,?,?);");

            ps.setString(1, UserSession.getInstance().getUsername());
            ps.setString(2, client.getType());
            ps.setTimestamp(3, UserSession.getInstance().getLastSync());
            ps.setBytes(4, client.getIpAddress());
            ps.setBytes(5, client.getMacAddress());

            ps.executeUpdate();
            System.out.println("Client with IP Address: " + client.getIpAddress().toString()
                    + " and MAC Address: " + client.getMacAddress().toString() + " were added! " + ps.toString());
        }
        catch (SQLException sqlex)
        {
            throw sqlex;
        }

        this.psclose();
    }

    public void removeUserClient(Client client) throws SQLException
    {
        try
        {
            ps = conn.prepareStatement("DELETE FROM user_client WHERE username = ? AND mac_address = ?");

            ps.setString(1, UserSession.getInstance().getUsername());
            ps.setBytes(2, client.getMacAddress());

            ps.executeUpdate();
        }
        catch (SQLException sqlex)
        {
            throw sqlex;
        }

        this.psclose();
    }

    public void updateUserClient(Client client) throws SQLException
    {
        try
        {
            ps = conn.prepareStatement("UPDATE user_client "
                    + "SET client_type = ?, last_sync = ?, ip_address = ? "
                    + "WHERE username = ? AND mac_address = ? ");

            ps.setString(1, client.getType());
            ps.setTimestamp(2, UserSession.getInstance().getLastSync());
            ps.setBytes(3, client.getIpAddress());
            ps.setString(4, UserSession.getInstance().getUsername());
            ps.setBytes(5, client.getMacAddress());

            ps.executeUpdate();

        }
        catch (SQLException sqlex)
        {
            throw sqlex;
        }
        this.psclose();
    }

    public void addClient(Client client) throws SQLException, SocketException, UnknownHostException
    {
        try
        {

            ps = conn.prepareStatement("INSERT INTO "
                    + "client (client_type, client_description, client_manufacturer, client_model_no) "
                    + "VALUES (?,?,?,?);");

            ps.setString(1, client.getType());
            ps.setString(2, client.getDescription());
            ps.setString(3, client.getManufacturer());
            ps.setString(4, client.getModelNo());

            ps.executeUpdate();

            System.out.println("Client with IP Address: " + client.getIpAddress().toString()
                    + " and MAC Address: " + client.getMacAddress().toString() + " were added! " + ps.toString());
        }
        catch (SQLException sqlex)
        {
            throw sqlex;
        }

        this.psclose();
    }

    public void removeClient(Client client) throws SQLException
    {
        try
        {
            ps = conn.prepareStatement("DELETE FROM client WHERE client_type = ?");

            ps.setString(1, client.getType());

            ps.executeUpdate();
        }
        catch (SQLException sqlex)
        {
            throw sqlex;
        }

        this.psclose();
    }

    public void updateClient(Client client) throws SQLException
    {
        try
        {
            ps = conn.prepareStatement("UPDATE client "
                    + "SET client_desc = ?, client_manufacturer = ?, client_model_no = ? "
                    + "WHERE client_type = ?");

            ps.setString(1, client.getDescription());
            ps.setString(2, client.getManufacturer());
            ps.setString(3, client.getModelNo());
            ps.setString(4, client.getType());

            ps.executeUpdate();

        }
        catch (SQLException sqlex)
        {
            throw sqlex;
        }
        this.psclose();
    }

    public HashMap<byte[], Client> getClientsByUser(User user) throws Exception
    {

        HashMap<byte[], Client> clientHashMap = new HashMap<>();

        try
        {
            ps = conn.prepareStatement("SELECT * FROM user_client JOIN client ON user_client.client_type=client.client_type WHERE username = ?");

            ps.executeUpdate();

            while (rs.next())
            {
                String clientType = rs.getString("client_type");
                String clientDesc = rs.getString("client_description");
                String clientManuf = rs.getString("client_manufacturer");
                String clientModelNo = rs.getString("client_model_no");
                byte[] clientIpAddress = rs.getBytes("ip_address");
                byte[] clientMacAddress = rs.getBytes("mac_address");
                Client newClient = new Client(clientType, clientDesc, clientManuf, clientModelNo, clientIpAddress, clientMacAddress);
                clientHashMap.put(clientMacAddress, newClient);
            }
        }
        catch (Exception sqlex)
        {
            throw sqlex;
        }
        this.psclose();

        return clientHashMap;
    }
}
