/*
 * Security is a singleton class that provides permissions 
 * structures and authentication for SmartEnrol
 */
package stockfile.security;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;


/**
 *
 */
public class Security {
    
    private static Security security = null;
    
    private Security() {
        
    }
    
     /**
     * Static method returns a single instance of UserSession
     * @return	a single instance of UserSession
     */
    public static Security getInstance()  {

        if (security == null) 
            security = new Security(); 
        
        return security;
    }
    
    /**
     *
     * @param stringToConvert
     * @return
     */
    public static String sha(String stringToConvert) {
         
        String sha = null;
         
        if(stringToConvert.isEmpty()) 
            return null;
         
        try {
            
            MessageDigest mDigest = MessageDigest.getInstance("SHA-512");

            mDigest.update(stringToConvert.getBytes(), 0, stringToConvert.length());

            sha = new BigInteger(1, mDigest.digest()).toString(16);
 
        } catch (NoSuchAlgorithmException ex) {
 
            ex.printStackTrace();
        }
        
        return sha;
    }
}

