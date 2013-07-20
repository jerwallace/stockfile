package stockfile.models;

import stockfile.security.UserSession;


/**
 * Class describing the Singleton FileList
 */
public class FileList
{

    private static FileList currentFileList = null;
    private Manifest manifest;

    /**
     * Singleton class constructor
     */
    protected FileList()
    {
        //fileList = new HashMap<String, StockFile>();
        this.manifest = new Manifest();
    }

    public static String convertToRelativePath(String fullPath) {
    	return fullPath.replace(UserSession.getInstance().getCurrentUser().getHomeDirectory(),"");
    }
    
    /**
     * Returns the only userList instance object
     */
    public static FileList getInstance()
    {

        if (currentFileList == null)
        {

            synchronized (FileList.class)
            {

                FileList inst = currentFileList;

                if (inst == null)
                {

                    synchronized (FileList.class)
                    {
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
