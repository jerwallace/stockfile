package stockfile.models;


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
