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
import stockfile.api.StockFile;
import stockfile.api.Manifest;
import stockfile.client.Client;
import static stockfile.dao.StockFileDAO.ps;
/**
 *
 * @author MrAtheist
 */
public class FileDAO extends StockFileDAO{
	
	public FileDAO () {
		super();
	}

	public int createFile(StockFile file) {

		this.initConnection();


		return 0;
	}

	public int updateFile(StockFile file) {

		this.initConnection();


		return 0;
	}

	public int removeFile(StockFile file) {

		this.initConnection();

		return 0;
	}

	public Manifest generateManifest(User user) {

		this.initConnection();
		Manifest manifest = new Manifest();


		return manifest;
	}
}