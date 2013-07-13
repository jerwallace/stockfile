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

    public void addClient() throws SQLException, SocketException, UnknownHostException
    {

        Client client = new Client();

        InetAddress ipAddress = InetAddress.getLocalHost();
        NetworkInterface nwi = NetworkInterface.getByInetAddress(ipAddress);
        byte macAddress[] = nwi.getHardwareAddress();
        System.out.println("IP Address: " + ipAddress.getAddress());
        System.out.println("MAC Address: " + macAddress);

        try
        {

            ps = conn.prepareStatement("INSERT INTO "
                    + "user_client (username,client_type,last_sync,ip_address,mac_address) "
                    + "VALUES (?,?,?,?,?);");

            ps.setString(1, UserSession.getInstance().getUserName());
            ps.setString(2, client.getType());
            ps.setString(3, UserSession.getInstance().getLastSync());
            ps.setBytes(4, ipAddress.getAddress());
            ps.setBytes(5, macAddress);

            int num = ps.executeUpdate();
            System.out.println(num + " records were added." + ps.toString());

        }
        catch (SQLException sqlex)
        {
            throw sqlex;
        }

        this.psclose();
        return 0;
    }

    public void removeClient()
    {



































































    public void removeClient()
    {

        Client client = new Client();


        this.psclose();
    }

    public ArrayList<Client> getClientsByUser(User user)
    {

        ArrayList<Client> clientList = new ArrayList<>();

        this.psclose();
        return clientList;
    }
}
