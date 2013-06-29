/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.client;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import stockfile.api.FileScanner;
import stockfile.client.protocol.AbstractProtocol;
import stockfile.client.protocol.CustomException;

/**
 * Abstract Client class
 */
public abstract class AbstractClient
{
    //Setting up connection parameters

    protected static final String HOST = "localhost";
    protected static final int PORT = 1099;
    protected static Registry registry;
    protected static AbstractProtocol thisProtocol;

    /**
     * Public method that runs the Abstract Client protocol
     * <p/>
     * @throws Exception
     */
    public static void run() throws Exception
    {
        //Scanner input = new Scanner(System.in);
        //String inputString = "";
//        //While User hasn't passed Exit message by chosing to Log Out do the following
//        while (!inputString.equalsIgnoreCase("Exit"))
//        {
//            String nextInstruction = "";
//            //Get next instruction based on the current state of the current user of the given type using the its protocol
//            nextInstruction = thisProtocol.getInstruction();
//
//
//            if (nextInstruction != null)
//            {
//                System.out.println(nextInstruction);
//                System.out.print("> ");
//                inputString = input.nextLine();
//            }
//            try
//            {
//                String serverOutput = thisProtocol.processInput(inputString);
//                if (serverOutput != null)
//                {
//                    System.out.println(serverOutput);
//                }
//            }
//            catch (NumberFormatException nfex)
//            {
//                System.err.println("Please enter a valid positive integer.");
//            }
//            catch (CustomException cex)
//            {
//                System.err.println(cex.getMessage());
//            }
//            catch (RemoteException ex)
//            {
//                System.err.println(ex);
//            }
//
//        }
    }

    /**
     * Public method that attempts to connect to Server using the entered Host
     * Name or IP address and Port Number
     */
    public static void connectToServer()
    {
        String validServerRegex = "[a-zA-Z0-9.]";
        Scanner input = new Scanner(System.in);
        String inputString = "";

        boolean isValidServer = false;

        while (!isValidServer)
        {

            System.out.println("Please enter a valid PBJ Stock Exchange server address (default: localhost):");
            System.out.print("> ");
            inputString = input.nextLine();

            if (inputString.isEmpty())
            {
                isValidServer = true;
            }
            else
            {
                Pattern rPattern = Pattern.compile(validServerRegex);
                Matcher matcher = rPattern.matcher(inputString);

                if (matcher.find())
                {
                    System.out.println("Connecting to " + inputString + "...");
                    UserSession.setHost(inputString);
                    isValidServer = true;
                }
                else
                {
                    System.err.println("Invalid hostname or IP address.");
                }
            }
        }
    }
}
