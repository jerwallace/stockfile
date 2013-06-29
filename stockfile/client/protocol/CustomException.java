/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.client.protocol;

/**
 *
 * @author WallaceJ
 */
public class CustomException extends Exception
{

    public enum ErrorType
    {
    }

    public CustomException(ErrorType type)
    {
        super(getMessage(type));
    }

    public static String getMessage(ErrorType type)
    {

        switch (type)
        {

        }
        return null;
    }
}
