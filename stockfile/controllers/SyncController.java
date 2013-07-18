/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.controllers;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import stockfile.dao.FileDAO;
import stockfile.models.FileList;
import stockfile.models.Manifest;
import stockfile.models.StockFile;
import stockfile.models.Manifest.Operation;
import stockfile.security.UserSession;

/**
 * 
 * @author WallaceJ
 */
public class SyncController {

	private FileDAO fileDAO = new FileDAO();
	private Map<String, Operation> syncList;
	private String homeDir = UserSession.getInstance().getCurrentUser()
			.getHomeDirectory();

	public SyncController() {

	}

	private void generateSyncList() {

		Map<String, StockFile> serverManifest = getServerManifest().manifest;
		Map<String, StockFile> clientManifest = FileList.getInstance()
				.getManifest().manifest;

		this.syncList = new TreeMap<>();

		if (clientManifest.isEmpty()) {

			for (String key : serverManifest.keySet()) {
				syncList.put(key, Operation.DOWNLOAD);
			}

		} else {
			
			for (String key : clientManifest.keySet()) {

				StockFile clientFile = clientManifest.get(key);

				if (!serverManifest.containsKey(key)) {
					syncList.put(key, Operation.UPLOAD);

				} else {

					StockFile servFile = serverManifest.get(key);

					if (servFile.getVersion() == clientFile.getVersion()) {
						syncList.put(key, Operation.UPLOAD_AND_OVERWRITE);
					} else if (servFile.getVersion() > clientFile.getVersion()) {
						syncList.put(key, Operation.DOWNLOAD_AND_OVERWRITE);
					} else {
						syncList.put(key, Operation.UPLOAD);
					}

					serverManifest.remove(key);
				}

			}

			for (String servkey : serverManifest.keySet()) {
				FileList.getInstance()
						.getManifest()
						.insertFile(homeDir + servkey,
								serverManifest.get(servkey));
				syncList.put(servkey, Operation.DOWNLOAD);
			}
		}

	}

	public void syncronize() {

		generateSyncList();
		System.out.println(syncList);

		if (syncList != null) {
			for (String key : this.syncList.keySet()) {
				try {
					switch (syncList.get(key)) {
					case DOWNLOAD:
					case DOWNLOAD_AND_OVERWRITE:
						System.out.println("Downloading " + key + "...");
						SFTPController.getInstance().get(key);
						break;
					case UPLOAD:
						System.out.println("Uploading " + key + "...");
						SFTPController.getInstance().send(key);
						fileDAO.updateFile(FileList.getInstance().getManifest()
								.getFile(key));
						break;
					case UPLOAD_AND_OVERWRITE:
						System.out.println("Uploading and overwriting " + key + "...");
						FileList.getInstance().getManifest().getFile(key).incrementVersion();
						SFTPController.getInstance().send(key);
						fileDAO.updateFile(FileList.getInstance().getManifest()
								.getFile(key));
						break;
					case DUPLICATE:
						System.out.println("Duplicating " + key + "...");
						SFTPController.getInstance().send(key);
						fileDAO.updateFile(FileList.getInstance().getManifest()
								.getFile(key));
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					Logger.getLogger(SyncController.class.getName()).log(
							Level.SEVERE, null, ex);
				}

			}
		}
	}

	private Manifest getServerManifest() {
		try {
			Manifest serverManifest = fileDAO.generateManifest();
			return serverManifest;
		} catch (SQLException ex) {
			Logger.getLogger(SyncController.class.getName()).log(Level.SEVERE,
					null, ex);
			return null;
		}
	}
}
