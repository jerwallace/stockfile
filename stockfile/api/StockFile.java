package stockfile.api;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Class describing a StockFile object
 */
public class StockFile extends File implements Serializable
{

    enum File_State
    {

        ACTIVE
    }
    private Timestamp lastSync;
    private File_State fileState;
    private String type;

    public StockFile(String filename)
    {
        super(filename);

        Date date = new Date();

        this.lastSync = new Timestamp(date.getTime());
        this.fileState = File_State.ACTIVE;

        int i = this.getName().lastIndexOf('.');
        if (i > 0)
        {
            this.type = this.getName().substring(i + 1);
        }
    }

    public String toString()
    {
        return "File: " + getName() + " | LM: " + lastModified() + " | " + this.lastSync + " | " + this.fileState + " | " + this.type + "\n";
    }
}
