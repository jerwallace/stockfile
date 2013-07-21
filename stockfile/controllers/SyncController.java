/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import stockfile.dao.FileDAO;
import stockfile.exceptions.ApplicationFailedException;
import stockfile.models.FileList;
import stockfile.models.Manifest;
import stockfile.models.StockFile;
import stockfile.models.Manifest.Operation;
import stockfile.security.UserSession;

/**
 * The sync controller contains the methods required to synchronize a local
 * folder with a server.
 *
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class SyncController {

    private FileDAO fileDAO = new FileDAO();
    private Map<String, Operation> syncList;
    private Manifest serverManifest;
    private Manifest clientManifest;
    private String homeDir = UserSession.getInstance().getCurrentClient()
            .getFullDir();
    private String userName = UserSession.getInstance().getCurrentUser().getUserName();
    private static SyncController syncController = null;
    private final int RECONNECTION_ATTEMPTS = 4;

    /**
     * Empty constructor.
     */
    private SyncController() {
    }

    /**
     * Static method returns a single instance of Sync Controller
     * <p/>
     * @return a single instance of Sync Controller
     */
    public static SyncController getInstance() {
        if (syncController == null) {
            syncController = new SyncController();
        }
        return syncController;
    }

    /**
     * This method compares two manifests and generates a sync list based on the
     * state. A file is considered in sync until it has been modified locally,
     * at that point it changes from increments its version and sets the insync
     * flag to false.
     */
    private void generateSyncList() {

        // Get the two manifests... One from the server, and one locally.
        serverManifest = getServerManifest();
        clientManifest = FileList.getInstance().getManifest();

        // This list of <File Key,Operation> to use in the synchronize method.
        this.syncList = new TreeMap<>();

        // If there are no files locally, download everything.
        if (clientManifest.manifest.isEmpty()) {

            for (String key : serverManifest.manifest.keySet())
                syncList.put(key, Operation.DOWNLOAD);

        } else {

            // Go through each file locally and find differences.
            for (String key : clientManifest.manifest.keySet()) {
            	
            	
            	
                StockFile clientFile = clientManifest.manifest.get(key);
                System.out.println("Last Sync Client:"+clientFile.getLastSyncTimeDB());
               
                // If the file is not on the server, upload it.
                if (!serverManifest.manifest.containsKey(key)) {
                	
                	// If it is a new file, upload it.
                	if (clientFile.getLastSyncTimeDB()==null) {
                		syncList.put(key, Operation.UPLOAD);
                	
                		// If it is an old file, it must have been deleted by someone else.
                	} else {
                		clientFile.delete();
                	}
                    // File is on server and stored locally.
                } else {

                    StockFile servFile = serverManifest.manifest.get(key);
                    System.out.println("Last Sync Server:"+servFile.getLastSyncTimeDB());
                    // SITUATION: 	Remove marker found.
                    // ACITON: 		Delete.
                    if (clientFile.hasRemoveMarker()) {
                        System.out.println("DELETE OPERATION INVOKED.");
                        syncList.put(key, Operation.DELETE);
                        serverManifest.manifest.remove(key);

                        // SITUATION: 	Version numbers the same and the file is in sync.
                        // ACTION: 		None.
                    } else if (servFile.getVersion() == clientFile.getVersion() && clientFile.inSync()) {
                        System.out.println("NO OPERATION INVOKED.");
                        syncList.put(key, Operation.NO_ACTION);
                        serverManifest.manifest.remove(key);

                        // SITUATION: 	InSync flag is false on a file (not a directory) AND last sync time on the server is after local file's last sync.
                        // ACTION: 		Duplicate, files are conflicting.
                    
                    } else if ((!clientFile.isDirectory()) && !(clientFile.inSync()) && servFile.getLastSyncTimeDB().isAfter(clientFile.getLastSyncTimeDB())) {
                        System.out.println("DUPLICATE OPERATION INVOKED.");
                        syncList.put(key, Operation.DUPLICATE);
                        serverManifest.manifest.remove(key);

                        // SITUATION: 	InSync flag is false and the last sync time in the DB is equal to or less than the local file.
                        //				(The user has updated their file and the changes need to be reflected)
                        // ACTION:		Upload and overwrite database file.
                    } else if (!(clientFile.inSync())) {
                        System.out.println("UPLOAD AND OVERWRITE OPERATION INVOKED.");
                        syncList.put(key, Operation.UPLOAD_AND_OVERWRITE);
                        serverManifest.manifest.remove(key);
                        // SITUATION:	The client file version is less than the server version and the file is inSync.
                        // ACTION:		The server has a later version, so download it!	
                    }
                }
            }

            System.out.println("Setting download settings: " + serverManifest);

            // SITUATION:	The file is on the server but is not in the FileList.
            // ACTION:		Download the file.
            for (String servKey : serverManifest.manifest.keySet()) {
                FileList.getInstance().getManifest().updateFile(homeDir + servKey, serverManifest.manifest.get(servKey));
                syncList.put(servKey, Operation.DOWNLOAD);
            }

        }

    }

    /**
     * Synchronize method synchronizes a folder by following a generated sync
     * list.
     *
     * @throws IOException If there is an error with the file stream.
     * @throws FileNotFoundException If a file can not be found while
     * downloading or uploading.
     * @throws SQLException If the database can not be reached or a record can
     * not be updated or removed.
     * @throws JSchException If an SFTP channel can not be established (all
     * servers down)
     * @throws SftpException If a file upload or download operation fails.
     * @throws InterruptedException If the current thread is interrupted.
     */
    public void syncronize() throws FileNotFoundException, IOException, SQLException, JSchException, InterruptedException, SftpException {

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

                    // Run the operations
                    try {
                        switch (operation) {
                            case DOWNLOAD:
                            case DOWNLOAD_AND_OVERWRITE:
                                System.out.println("Downloading " + key + "...");
                                SFTPController.getInstance(userName).download(key);
                                FileList.getInstance().getManifest().updateFile(serverManifest.manifest.get(key).getRelativePath(), serverManifest.manifest.get(key));
                                break;
                            case UPLOAD:
                                System.out.println("Uploading " + key + "...");
                                if (SFTPController.getInstance(userName).upload(key)) {
                                    fileDAO.updateFile(FileList.getInstance().getManifest()
                                            .getFile(key));
                                }
                                break;
                            case UPLOAD_AND_OVERWRITE:
                                System.out.println("Uploading and overwriting " + key + "...");
                                if (SFTPController.getInstance(userName).upload(key)) {
                                    fileDAO.updateFile(FileList.getInstance().getManifest()
                                            .getFile(key));
                                }
                                break;
                            case DUPLICATE:
                                System.out.println("Duplicating " + key + "...");
                                SFTPController.getInstance(userName).duplicate(key);
                                fileDAO.updateFile(FileList.getInstance().getManifest()
                                        .getFile(key));
                                break;
                            case DELETE:
                                System.out.println("Deleting file " + key + "...");
                                SFTPController.getInstance(userName).delete(key);
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

                        // Reset the sync flag to true after sync operation is complete on the file.
                        if (FileList.getInstance().getManifest().containsFile(key)) 
                            FileList.getInstance().getManifest().getFile(key).resetSync();
  
                        success = true;

                    } catch (SftpException ex) {

                        // SITUATION: 	File was not found on the server, but the database records exist.
                        //				It was likely deleted by an administrator without changing the database.
                        //				Try to flip the operation (upload the local file) to replace the file.
                        if (ex.id == 2) {
                            if (attempts < RECONNECTION_ATTEMPTS) {
                                System.err.println("File not found. Flipping operation...");
                                System.err.println("Attempt #" + attempts + "...");
                                operation = flipOperation(operation);
                                attempts++;
                                continue;
                            } else {
                                throw ex;
                            }

                            // SITUATION:	Failed connection.
                            //				The master server you are connecting to has failed or the connection timed out.
                            // ACTION:		Retry the connection up to three times
                        } else {
                            if (attempts < RECONNECTION_ATTEMPTS) {
                                System.err.println(ex + "" + ex.getStackTrace());
                                SFTPController.getInstance(userName).reconnect();
                                attempts++;
                                continue;

                                // SITUATION:	All servers are down.
                                // ACTION:		Exit the program.
                            } else {
                                throw new ApplicationFailedException("No valid servers.");
                            }
                        }
                    } catch (SQLException sqlex) {
                    	System.err.println("Problem running SQL query. Refreshing queue and trying again momentarily.");
                    }
                }
            }
        }
    }

    /**
     * This method flips the operation.
     *
     * @param thisOp Takes an operation and gives the opposite operation.
     * @return the opposite operation.
     */
    private Operation flipOperation(Operation thisOp) {
        switch (thisOp) {
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

    /**
     * Gets the server manifest from the database.
     *
     * @return The server manifest.
     */
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
