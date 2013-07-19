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

import com.jcraft.jsch.SftpException;

import stockfile.dao.FileDAO;
import stockfile.exceptions.ApplicationFailedException;
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
						.updateFile(homeDir + servkey,
								serverManifest.get(servkey));
				syncList.put(servkey, Operation.DOWNLOAD);
			}
		}

	}

	public void syncronize() throws Exception {

		generateSyncList();
		System.out.println(syncList);
		
		// Start at the first attempt and assume it failed.
		boolean success = false;
		int attempts = 1;
		
		if (syncList != null) {
			
			for (String key : this.syncList.keySet()) {
				
				Operation operation = syncList.get(key);
				
				// Reset attempts and success flag.
				attempts = 1;
				success = false;
				
				while (!success) {
					
					try {
						switch (operation) {
						case DOWNLOAD:
						case DOWNLOAD_AND_OVERWRITE:
							System.out.println("Downloading " + key + "...");
							SFTPController.getInstance().get(key);
							break;
						case UPLOAD:
							System.out.println("Uploading " + key + "...");
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
						
						success = true;
						
					} catch (SftpException ex) {
						if (ex.id == 2) {
							if (attempts < 3) {
								System.err.println("File not found. Flipping operation...");
								System.err.println("Attempt #"+attempts+"...");
								operation = flipOperation(operation);
							} else {
								throw ex;
							}
						} else {
							if (attempts < 3) {
								SFTPController.getInstance().reconnect();
							} else {
								throw new ApplicationFailedException("No valid servers.");
							}
						}
						continue;
					}
				}

			}
		}
	}
	
	private Operation flipOperation(Operation thisOp) {
		switch(thisOp) {
			case DOWNLOAD_AND_OVERWRITE:
			case DOWNLOAD:
				return Operation.UPLOAD;
			case UPLOAD_AND_OVERWRITE:
			case UPLOAD:
				return Operation.DOWNLOAD;
			default:
				return Operation.DUPLICATE;
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
