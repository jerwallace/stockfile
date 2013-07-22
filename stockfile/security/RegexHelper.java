package stockfile.security;

import java.util.regex.*;

/**
 * This is a singleton class for regex.
 */
public class RegexHelper {

    public enum RegExPattern {

        EMAIL, USERNAME, PASSWORD, ALPHABETS, NAME, TEXT, TYPE, FOLDERPATH;
    }
    private static final String RGX_USERNAME = "^[a-z0-9_-]{5,30}$";
    private static final String RGX_EMAIL = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$";
    private static final String RGX_ALPHABETS = "^[a-zA-Z ]{1,50}$";
    private static final String RGX_PASSWORD = "\\w{8,30}";
    private static final String RGX_TEXT = "\\w{0,30}";
    private static final String RGX_TYPE = "\\w{1,30}";
    private static final String RGX_NAME = "[A-Za-z0-9_~\\-'\\$%\\&]+$";
//    private static final String RGX_FOLDERPATH = "^((([a-zA-Z]:)(\\\\{2}[a-zA-Z]+)(\\\\{2}\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}))(?=(\\\\(\\w[\\w ]*)))(\\\\\\w[\\w ]*)*)$";
//    private static final String RGX_FOLDERPATH = "^(?:[a-zA-Z]\\:|\\\\\\\\[\\w\\.]+\\\\[\\w.$]+)\\\\(?:[\\w]+\\\\)*\\w([\\w.])+$";
//    private static final String RGX_FOLDERPATH = "^[^\\\\/?%*:|\"<>\\.]+$";
    private static final String RGX_FOLDERPATH = "^[^(\\/)?%*:|\"<>\\.]*$";
    private static RegexHelper regex = null;

    protected RegexHelper() {
    }	// empty 

    /**
     * Static method returning a single instance of RegexHelper
     *
     * @return a single instance of RegexHelper
     */
    public static RegexHelper getInstance() {

        if (regex == null)
            regex = new RegexHelper();

        return regex;
    }

    /**
     * Static method comparing the given input to the regex pattern
     *
     * @param input
     * @param regEx
     * @return true if the given input matches to the regex pattern
     */
    public static Boolean validate(String input, RegExPattern regEx) {

        String patternToUse;

        if (input == null || regEx == null)
            return true;

        switch (regEx) {
            case USERNAME:
                patternToUse = RGX_USERNAME;
                break;
            case PASSWORD:
                patternToUse = RGX_PASSWORD;
                break;
            case EMAIL:
                patternToUse = RGX_EMAIL;
                break;
            case ALPHABETS:
                patternToUse = RGX_ALPHABETS;
                break;
            case NAME:
                patternToUse = RGX_NAME;
                break;
            case TEXT:
                patternToUse = RGX_TEXT;
                break;
            case TYPE:
                patternToUse = RGX_TYPE;
                break;
            case FOLDERPATH:
                patternToUse = RGX_FOLDERPATH;
                break;
            default:
                patternToUse = null;
                break;
        }

        if (patternToUse != null) {

            Pattern rPattern = Pattern.compile(patternToUse);
            Matcher matcher = rPattern.matcher(input);

            if (matcher.find())
                return true;
            else
                return false;
            
        } else {
            System.err.println("No regular expression could be found.");
            return true;
        }
    }
}
