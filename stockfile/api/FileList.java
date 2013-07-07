package stockfile.api;

/**
 * Class describing the Singleton FileList
 */
public class FileList
{

    private static FileList currentFileList = null;
    private static Manifest manifest;

    /**
     * Singleton class constructor
     */
    protected FileList()
    {
        //fileList = new HashMap<String, StockFile>();
        FileList.manifest = new Manifest();
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
}
