package edu.scu;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
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

    public static InetSocketAddress requestAddress (InetSocketAddress server, String req)
    {

        // invalid input, return null
        if (server == null || req == null) {
            return null;
        }

        // send request to server
        String response = sendRequest(server, req);

        // if response is null, return null
        if (response == null) {
            return null;
        }

        // or server cannot find anything, return server itself
        else if (response.startsWith("NOTHING"))
            return server;

            // server find something,
            // using response to create, might fail then and return null
        else {
            InetSocketAddress ret = Util.createSocketAddress(response.split("_")[1]);
            return ret;
        }
    }

    public static String sendRequest(InetSocketAddress server, String req)
    {

        // invalid input
        if (server == null || req == null)
            return null;

        Socket talkSocket = null;

        // try to open talkSocket, output request to this socket
        // return null if fail to do so
        try {
            talkSocket = new Socket(server.getAddress(),server.getPort());
            PrintStream output = new PrintStream(talkSocket.getOutputStream());
            output.println(req);
        } catch (IOException e) {
            //System.out.println("\nCannot send request to "+server.toString()+"\nRequest is: "+req+"\n");
            return null;
        }

        // sleep for a short time, waiting for response
        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // get input stream, try to read something from it
        InputStream input = null;
        try {
            input = talkSocket.getInputStream();
        } catch (IOException e) {
            System.out.println("Cannot get input stream from "+server.toString()+"\nRequest is: "+req+"\n");
        }
        String response = Util.inputStreamToString(input);

        // try to close socket
        try {
            talkSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Cannot close socket", e);
        }
        return response;
    }

    public static String inputStreamToString (InputStream in) {

        // invalid input
        if (in == null) {
            return null;
        }

        // try to read line from input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            System.out.println("Cannot read line from input stream.");
            return null;
        }

        return line;
    }


}
