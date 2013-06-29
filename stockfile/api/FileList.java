package stockfile.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Class describing the Singleton FileList
 */
public class FileList
{

    private static FileList currentFileList = null;
    private static HashMap fileList;

    /**
     * Singleton class constructor
     */
    protected FileList()
    {
        fileList = new HashMap<String, StockFile>();
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
//
//    public HashMap<String, StockFile> getFileList()
//    {
//        return this.fileList;
//    }
}
