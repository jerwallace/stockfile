/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import stockfile.api.User;
import stockfile.client.Client;
import static stockfile.dao.StockFileDAO.ps;
import static stockfile.dao.StockFileDAO.rs;

/**
 *
 * @author MrAtheist
 */
public class ClientDAO extends StockFileDAO{
	
	public ClientDAO () {
		super();
	}

	public int addClient() {

		this.initConnection();
		Client client = new Client();


		return 0;
	} 


	public int removeClient() {

		this.initConnection();
		Client client = new Client();


		return 0;
	} 

	public ArrayList<Client> getClientsByUser(User user) {

		this.initConnection();
		ArrayList<Client> clientList = new ArrayList<>();


		return clientList;
	}
}
