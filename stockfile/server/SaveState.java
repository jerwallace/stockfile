/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;
import stockfile.api.FileList;
import stockfile.api.UserList;

/**
 *
 * @author WallaceJ
 */
public class SaveState
{

    String tmpDir = System.getProperty("user.dir");
    ArrayList<Map> mapsToSave = new ArrayList<>();

    /**
     *
     */
    public void saveState()
    {

        mapsToSave.add(UserList.getInstance().exportUserList());

        try
        {
            try (
                    FileOutputStream fileOut = new FileOutputStream(this.tmpDir + "pbjdata.ser");
                    ObjectOutputStream out = new ObjectOutputStream(fileOut))
            {
                out.writeObject(mapsToSave);
            }
        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
    }

    /**
     *
     */
    public void loadState()
    {

        File f = new File(this.tmpDir + "pbjdata.ser");

        if (f.exists())
        {
            try
            {
                //use buffering
                InputStream file = new FileInputStream(this.tmpDir + "pbjdata.ser");
                InputStream buffer = new BufferedInputStream(file);
                try (ObjectInput input = new ObjectInputStream(buffer))
                {
                    //deserialize the List
                    mapsToSave = (ArrayList<Map>) input.readObject();

                    UserList.getInstance().importUserList(mapsToSave.get(0));
                    //display its data
                    System.out.println("Stock List Imported:");
                    System.out.println(FileList.getInstance());
                    System.out.println("User List Imported:");
                    System.out.println(UserList.getInstance());
                }
            }
            catch (ClassNotFoundException ex)
            {
                System.err.println("Cannot perform input. Class not found.");
            }
            catch (IOException ex)
            {
                System.err.println("Cannot perform input.");
            }
        }
        else
        {
            System.err.println("No saved state could be found. Creating new instance.");
        }
    }
}
