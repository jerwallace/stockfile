/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.Stack;

import org.apache.commons.io.FileUtils;

import stockfile.dao.FileDAO;
import stockfile.exceptions.ApplicationFailedException;
import stockfile.models.Manifest;
import stockfile.models.StockFile;
import stockfile.models.FileList;
import stockfile.security.UserSession;

/**
 * The state controller loads and saves PBJ files and the contents of a
 * directory into the application.
 *
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class StateController {

    // Name of the state file to save locally.
    private final String DATA_FILE_NAME = "/stockdata.pbj";
    // The client's home directory.
    private final String HOME_DIR = UserSession.getInstance().getCurrentClient().getFullDir();
    // The manifest of the file list stored in memory.
    private Manifest currentManifest = FileList.getInstance().getManifest();
    private FileDAO fileDAO = new FileDAO();
    private static StateController sc = null;

    private StateController() {
        // Empty constructor
    }

    /**
     * Static method returns a single instance of State Controller.
     * <p/>
     * @return a single instance of State Controller.
     */
    public static StateController getInstance() {
        if (sc == null) {
            sc = new StateController();
        }
        return sc;
    }

    /**
     * This method saves the state of the system by serializing the singleton
     * FileList to a .pbj file in the root stockfiles local directory.
     */
    public void saveState() {
        try {
            try (
                    FileOutputStream fileOut = new FileOutputStream(this.HOME_DIR + DATA_FILE_NAME);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(currentManifest);
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * This method scans through a local directory and looks for discrepancies
     * between the local file list. If it is not in the list, it is added to the
     * list.
     *
     * @param dirToScan A directory to scan.
     */
    public void loadDirectoryState(String dirToScan) {

        try {

            Path startPath = Paths.get(dirToScan);
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                    StockFile thisFile = new StockFile(dir.toString(), null);
                    if (!thisFile.getRelativePath().equals("")) {
                        FileList.getInstance().getManifest().insertFile(thisFile.getRelativePath(), thisFile);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    StockFile thisFile = new StockFile(file.toString(), null);
                    System.out.println("Visited file" + file.toString());
                    FileList.getInstance().getManifest().insertFile(thisFile.getRelativePath(), thisFile);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) {
                    System.err.println("Visiting file " + file.toString() + " failed.");
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method loads a .pbj file that contains the last manifest.
     */
    public void loadState() throws ApplicationFailedException {

        // Check if the PBJ file exists.
        File f = new File(this.HOME_DIR + DATA_FILE_NAME);

        if (f.exists()) {
            try {
                // Buffer the PBJ file to insert it into the singleton File List.
                InputStream file = new FileInputStream(this.HOME_DIR + DATA_FILE_NAME);
                InputStream buffer = new BufferedInputStream(file);
                try (ObjectInput input = new ObjectInputStream(buffer)) {
                    // Deserialize the object and retain the manifest from the PBJ file.
                    currentManifest = (Manifest) input.readObject();

                    // Load the manifest into the FileList.
                    FileList.getInstance().loadManifest(currentManifest);


                    System.out.println("Checking for any deleted items on the server...");

                    // A list of files to remove from the system.
                    Stack<String> removeList = new Stack<String>();

                    // If the manifest in the PBJ file contains a file that is not in the database, it 
                    // means at one point it was in the database and was removed. Therefore, it has been deleted.
                    for (String key : FileList.getInstance().getManifest().manifest.keySet()) {
                        StockFile thisFile = FileList.getInstance().getManifest().manifest.get(key);
                        if (!fileDAO.inDatabase(thisFile)) {
                            if (!SFTPController.getInstance(UserSession.getInstance().getCurrentUser().getUserName()).inBlackList(thisFile.getName())) {
                                System.out.println("File" + thisFile.getAbsolutePath() + " was deleted on the server.");
                                if (thisFile.isDirectory()) {
                                    FileUtils.deleteDirectory(thisFile);
                                } else if (thisFile.exists()) {
                                    thisFile.delete();
                                }
                                removeList.add(key);
                            }
                        }
                    }

                    // Remove the files from the local file list.
                    while (!removeList.empty()) {
                        FileList.getInstance().getManifest().removeFile(removeList.pop());
                    }

                    System.out.println("Local Manifest:");
                    System.out.println(FileList.getInstance().getManifest());
                }
            } catch (ClassNotFoundException ex) {
                System.err.println("Cannot perform input. Class not found.");
            } catch (IOException ex) {
                System.err.println("Cannot perform input." + ex);
            } catch (SQLException ex) {
                throw new ApplicationFailedException("Could not connect to database.");
            }
        } else {
            System.err.println("No saved state could be found. Creating new instance.");
        }
    }
}
