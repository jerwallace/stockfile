package stockfile.api;

/**
 * Class describing the Singleton FileList
 */
public class FileList
{

    private static FileList currentFileList = null;

    /**
     * Singleton class constructor
     */
    protected FileList()
    {
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
