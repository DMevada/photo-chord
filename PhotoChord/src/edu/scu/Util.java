package edu.scu;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author Raghav Bhandari
 * @author Krishna Kandhani
 * @author Abhiman Kolte
 * @author Dhruv Mevada
 */
public class Util
{
    public static InetSocketAddress createSocketAddress(String address)
    {
        if (address == null)
        {
            return null;
        }

        // split input
        String[] parts = address.split(":");

        //split string contains ":"
        if (parts.length == 2) {

            //get and pre-process ip address string
            String ip = parts[0];

            if (ip.charAt(0) == '/')
            {
                ip = ip.substring(1);
            }

            InetAddress ipAddress = null;

            try
            {
                ipAddress = InetAddress.getByName(ip);
            }

            catch (UnknownHostException e)
            {
                System.out.println("Cannot create ip address: "+ip);
                return null;
            }

            int port = Integer.parseInt(parts[1]);
            return new InetSocketAddress(ipAddress, port);
        }

        // split string does not contain ":"
        else
        {
            return null;
        }

    }

}
