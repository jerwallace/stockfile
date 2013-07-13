/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import stockfile.api.User;
import stockfile.client.Client;
import stockfile.client.UserSession;

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

    public void addClient(Client client) throws SQLException, SocketException, UnknownHostException
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

            int num = ps.executeUpdate();
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

    public void updateClient(Client client) throws SQLException
    {
        try
        {
            ps = conn.prepareStatement("UPDATE client "
                    + "SET client_type = ?, client_desc = ?, client_manufacturer = ?, client_model_no = ? "
                    + "WHERE username = ? AND mac_address = ? ");

            ps.setString(1, client.getType());
            ps.setString(2, client.getDescription());
            ps.setString(3, client.getManufacturer());
            ps.setString(4, client.getModelNo());

            ps.executeUpdate();

        }
        catch (SQLException sqlex)
        {
            throw sqlex;
        }
        this.psclose();
    }

    public ArrayList<Client> getClientsByUser(User user)
    {

        ArrayList<Client> clientList = new ArrayList<>();

        this.psclose();
        return clientList;
    }
}
