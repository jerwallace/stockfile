/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.controllers;

import java.io.FileNotFoundException;
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
	private Manifest serverManifest;
	private Manifest clientManifest;
	private String homeDir = UserSession.getInstance().getCurrentUser()
			.getHomeDirectory();
	private static SyncController syncController = null;

	private SyncController() {

	}
	
	/**
     * Static method returns a single instance of MySQLConnection.
     * <p/>
     * @return a single instance of MySQLConnection
     */
    public static SyncController getInstance()
    {
        if (syncController == null)
        {
        	syncController = new SyncController();
        }
        return syncController;
    }

	private void generateSyncList() {
		
		serverManifest = getServerManifest();
		clientManifest = FileList.getInstance().getManifest();

		this.syncList = new TreeMap<>();

		if (clientManifest.manifest.isEmpty()) {

			for (String key : serverManifest.manifest.keySet()) {
				syncList.put(key, Operation.DOWNLOAD);
			}

		} else {
			
			for (String key : clientManifest.manifest.keySet()) {

				StockFile clientFile = clientManifest.manifest.get(key);

				if (!serverManifest.manifest.containsKey(key)) {
					syncList.put(key, Operation.UPLOAD);

				} else {

					StockFile servFile = serverManifest.manifest.get(key);

					if (clientFile.hasRemoveMarker()) {
						syncList.put(key, Operation.DELETE);
					} else if (servFile.getVersion() == clientFile.getVersion()) {
						syncList.put(key, Operation.NO_ACTION);
					} else {
						syncList.put(key, Operation.UPLOAD);
					}

					if (!(servFile.getVersion() > clientFile.getVersion())) {
						serverManifest.manifest.remove(key);
					}
				}

			}

			for (String servkey : serverManifest.manifest.keySet()) {
					FileList.getInstance()
						.getManifest()
						.updateFile(homeDir + servkey,
								serverManifest.manifest.get(servkey));
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
							FileList.getInstance().getManifest().updateFile(serverManifest.manifest.get(key).getRelativePath(),serverManifest.manifest.get(key));
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
						case DELETE:
							System.out.println("Deleting file " + key + "...");
							SFTPController.getInstance().delete(key);
							fileDAO.removeFile(FileList.getInstance().getManifest()
									.getFile(key));
							FileList.getInstance().getManifest().removeFile(key);
							break;
						case NO_ACTION:
							System.out.println("No action is being performed on " + key + "...");
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
								attempts++;
								continue;
							} else {
								throw ex;
							}
						} else {
							if (attempts < 3) {
								System.err.println(ex+""+ex.getStackTrace());
								SFTPController.getInstance().reconnect();
								attempts++;
								continue;
							} else {
								throw new ApplicationFailedException("No valid servers.");
							}
						}
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
