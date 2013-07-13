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
public class ClientDAO extends StockFileDAO{
	
	public ClientDAO () {
		super();
	}

	public void addClient() throws SQLException, SocketException, UnknownHostException {

		Client client = new Client();
                InetAddress address = InetAddress.getLocalHost();
                NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
                byte mac[] = nwi.getHardwareAddress();
                System.out.println(mac);

                this.psclose();
	} 


	public void removeClient() {

		Client client = new Client();


		this.psclose();
	} 

	public ArrayList<Client> getClientsByUser(User user) {

		ArrayList<Client> clientList = new ArrayList<>();

                this.psclose();
		return clientList;
	}
}
