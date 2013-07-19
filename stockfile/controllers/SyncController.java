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
import sandbox.gateway.models.Servers.ServerList;
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
<<<<<<< HEAD
		Map<String, StockFile> clientManifest = ServerList.getInstance().getManifest().manifest;
=======
		Map<String, StockFile> clientManifest = FileList.getInstance()
				.getManifest().manifest;
>>>>>>> ada9c0ae29d1625cce2b4f224ab3fd833649735b

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
<<<<<<< HEAD
//						if (clientFile.getLastModified().isAfter(
//								servFile.getLastModified())) {
							ServerList.getInstance().getManifest().getFile(key)
									.incrementVersion();
							syncList.put(key, Operation.UPLOAD);
//						}
=======
						syncList.put(key, Operation.UPLOAD_AND_OVERWRITE);
					} else if (servFile.getVersion() > clientFile.getVersion()) {
						syncList.put(key, Operation.DOWNLOAD_AND_OVERWRITE);
>>>>>>> ada9c0ae29d1625cce2b4f224ab3fd833649735b
					} else {
						syncList.put(key, Operation.UPLOAD);
					}

<<<<<<< HEAD
				for (String servkey : serverManifest.keySet()) {
					ServerList.getInstance().getManifest()
							.insertFile(homeDir+servkey,serverManifest.get(servkey));
					syncList.put(servkey, Operation.DOWNLOAD);
=======
					serverManifest.remove(key);
>>>>>>> ada9c0ae29d1625cce2b4f224ab3fd833649735b
				}

			}

			for (String servkey : serverManifest.keySet()) {
				FileList.getInstance()
						.getManifest()
						.updateFile(homeDir + servkey,
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
						if (SFTPController.getInstance().get(key));
						break;
					case UPLOAD:
						System.out.println("Uploading " + key + "...");
<<<<<<< HEAD
						SFTPController.getInstance().send(key);
						fileDAO.updateFile(ServerList.getInstance().getManifest()
								.getFile(key));
=======
						if (SFTPController.getInstance().send(key)) {
							fileDAO.updateFile(FileList.getInstance().getManifest()
									.getFile(key));
						}
						break;
					case UPLOAD_AND_OVERWRITE:
						System.out.println("Uploading and overwriting " + key + "...");
						FileList.getInstance().getManifest().getFile(key).incrementVersion();
						if (SFTPController.getInstance().send(key)) {
							fileDAO.updateFile(FileList.getInstance().getManifest()
									.getFile(key));
						}
>>>>>>> ada9c0ae29d1625cce2b4f224ab3fd833649735b
						break;
					case DUPLICATE:
						System.out.println("Duplicating " + key + "...");
						SFTPController.getInstance().send(key);
						fileDAO.updateFile(ServerList.getInstance().getManifest()
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
