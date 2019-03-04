package edu.scu;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Raghav Bhandari
 * @author Krishna Kandhani
 * @author Abhiman Kolte
 * @author Dhruv Mevada
 */

public class Chord {

    private InetAddress _contact;
    private int port;

    private void parseArguments(String[] args)
    {
        //list nodes in chord ring
        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("list"))
            {
                //display list of existing nodes in chord ring
            }

            else
            {
                printHelp();
            }
        }

        //create a chord ring
        else if(args.length == 2)
        {
            if(args[0].equalsIgnoreCase("create"))
            {
                port = Integer.parseInt(args[1]);

                if(port > 65536 || port < 1000)
                {
                    Logger.log("Port must be between 1001 and 65536, cannot create chord ring");
                    printHelp();
                }

                else
                {
                    //create chord ring
                }
            }

            else
            {
                printHelp();
            }
        }

        //join an existing chord ring
        else if(args.length == 3)
        {
            if(args[0].equalsIgnoreCase("join"))
            {
                port = Integer.parseInt(args[1]);

                if(port > 65536 || port < 1000)
                {
                    Logger.log("Port must be between 1001 and 65536, cannot create chord ring");
                    printHelp();
                }

                String ipPort = args[2];

                if(ipPort.contains(":"))
                {
                    //join existing chord ring
                }

                else
                {
                    printHelp();
                }
            }

            else
            {
                printHelp();
            }
        }

        else
        {

            printHelp();
        }
    }

    private void printHelp()
    {
        Logger.log("Cannot determine command, will not start\n");
        Logger.log("Usage: java Chord <list> <create> <join>\n");
        Logger.log("Please specify a command from one of the formats below.");
        Logger.log("    1) java list");
        Logger.log("    2) java create <port>");
        Logger.log("    3) java join <port> <ip:port>");
    }

    private String getOwnIp()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        }

        catch (UnknownHostException error)
        {
            error.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) throws UnknownHostException
    {
	    Chord chord = new Chord();
	    chord.parseArguments(args);

	    String ipAddressPort = chord.getOwnIp() + ":" + chord.port;
	    Logger.log(ipAddressPort);


    }

}