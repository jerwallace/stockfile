package stockfile.models;

import stockfile.security.StockFileSession;

/**
 * A singleton file list that is serialized into a PBJ file.
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class FileList {

    private static FileList currentFileList = null;
    private Manifest manifest;

    /**
     * Singleton class constructor
     */
    protected FileList() {
        //fileList = new HashMap<String, StockFile>();
        this.manifest = new Manifest("Local PBJ File List Manifest");
    }

    public static String convertToRelativePath(String fullPath) {
        return fullPath.replace(StockFileSession.getInstance().getCurrentClient().getFullDir(), "");
    }

    /**
     * Returns the only userList instance object
     */
    public static FileList getInstance() {

        if (currentFileList == null) {

            synchronized (FileList.class) {

                FileList inst = currentFileList;

                if (inst == null) {

                    synchronized (FileList.class) {
                        currentFileList = new FileList();
                    }
                }
            }
        }

        return currentFileList;

    }

    public Manifest getManifest() {
        return this.manifest;
    }

    public void loadManifest(Manifest manifest) {
        this.manifest = manifest;
    }
}
