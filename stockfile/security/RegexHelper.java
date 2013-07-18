/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.security;

import java.util.regex.*;

/**
 * This is a singleton class for regex.
 */
public class RegexHelper {

    public enum RegExPattern {

        POSTAL_CODE, PHONE_NUMBER, EMAIL, FLOAT, INT, USERNAME, PASSWORD,
		ALPHABETS, COURSE_NUMBER, LETTER_DIGIT, UPPSERCASE_LETTER, NAME
    }
    private static final String RGX_USERNAME = "^[a-z0-9_-]{5,30}$";
    private static final String RGX_PASSWORD = ".{8,30}";
    private static final String RGX_EMAIL = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$";
    private static final String RGX_POSTAL_CODE = "[A-Za-z0-9\\-]+$";
    private static final String RGX_FLOAT = "^(?=.+)(?:[1-9]\\d*|0)?(?:\\.\\d+)?$";
    private static final String RGX_INT = "^[1-9]\\d*$";
    private static final String RGX_PHONE_NUMBER = "^\\(?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$";
    private static final String RGX_COURSE_NUMBER = "^\\d{1,11}";
    private static final String RGX_ALPHABETS = "^^[a-zA-Z ]{1,50}$";
    private static final String RGX_LETTER_DIGIT = "\\w" ;
    private static final String RGX_UPPERCASE_LETTER = "^[A-Z]*$" ;
    private static final String RGX_NAME = "[A-Za-z0-9_~\\-'\\$%\\&]+$";
    private static RegexHelper regex = null;

    protected RegexHelper() {
    }	// empty 

    /**
     * Static method returning a single instance of RegexHelper
     *
     * @return a single instance of RegexHelper
     */
    public static RegexHelper getInstance() {

        if (regex == null) {
            regex = new RegexHelper();
        }

        return regex;
    }

    public static Boolean validate(String input, RegExPattern regEx) {

        String patternToUse;

        if (input == null || input.isEmpty() || regEx == null) {
            return true;
        }

        switch (regEx) {
            case USERNAME:
                patternToUse = RGX_USERNAME;
                break;
            case PASSWORD:
                patternToUse = RGX_PASSWORD;
                break;
            case POSTAL_CODE:
                patternToUse = RGX_POSTAL_CODE;
                break;
            case PHONE_NUMBER:
                patternToUse = RGX_PHONE_NUMBER;
                break;
            case FLOAT:
                patternToUse = RGX_FLOAT;
                break;
            case INT:
                patternToUse = RGX_INT;
                break;
            case EMAIL:
                patternToUse = RGX_EMAIL;
                break;
            case COURSE_NUMBER:
                patternToUse = RGX_COURSE_NUMBER;
                break;
            case ALPHABETS:
                patternToUse = RGX_ALPHABETS;
                break;
            case LETTER_DIGIT:
                patternToUse = RGX_LETTER_DIGIT;
                break;          
            case UPPSERCASE_LETTER:
                patternToUse = RGX_UPPERCASE_LETTER;
                break;
            case NAME:
                patternToUse = RGX_NAME;
                break;
            default:
                patternToUse = null;
                break;
        }

        if (patternToUse != null) {

            Pattern rPattern = Pattern.compile(patternToUse);
            Matcher matcher = rPattern.matcher(input);

            if (matcher.find()) {
                return true;
            } else {
                return false;
            }
        } else {
            System.err.println("No regular expression could be found.");
            return true;
        }
    }
}
