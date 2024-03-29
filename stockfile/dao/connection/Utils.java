/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao.connection;

/**
 *
 * @author Jeremy
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * * Set of common utilities.
 *
 * @author faheem *
 */
public class Utils {

    /**
     * * Read a properties file from the classpath and return a Properties
     * object
     *
     * @param filename
     * @return
     * @throws IOException
     */
    static public Properties readProperties(String filename) throws IOException {
        
        Properties props = new Properties();
        InputStream stream = Utils.class.getResourceAsStream(filename);
        props.load(stream);
        return props;
    }
}
