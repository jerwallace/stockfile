
package stockfile.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

/**
 * Security class contains the methods needed to sign to the system.
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class Security {

    private static Security security = null;

    private Security() {
    }

    /**
     * Static method returns a single instance of Security
     *
     * @return	a single instance of UserSession
     */
    public static Security getInstance() {

        if (security == null)
            security = new Security();

        return security;
    }

    /**
     * Returns a SHA-256 hashed version of the given string
     *
     * @param stringToConvert
     * @return
     */
    public static String sha(String stringToConvert) {

        String sha = null;

        if (stringToConvert.isEmpty())
            return null;

        try {

            MessageDigest mDigest = MessageDigest.getInstance("SHA-256");

            mDigest.update(stringToConvert.getBytes(), 0, stringToConvert.length());

            sha = new BigInteger(1, mDigest.digest()).toString(16);

        } catch (NoSuchAlgorithmException ex) {

            ex.printStackTrace();
        }

        return sha;
    }
}
