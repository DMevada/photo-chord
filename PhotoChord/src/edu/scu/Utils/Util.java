package edu.scu.Utils;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static edu.scu.Utils.Constants.sizeOfFingerTable;

/**
 * @author Raghav Bhandari
 * @author Krishna Kandhani
 * @author Abhiman Kolte
 * @author Dhruv Mevada
 */

public class Util {
    private static HashMap<Integer, Long> powerOfTwo = null;

    /**
     * Constructor
     */
    public Util() {
        //initialize power of two table
        powerOfTwo = new HashMap<Integer, Long>();
        long base = 1;
        for (int i = 0; i <= sizeOfFingerTable; i++) {
            powerOfTwo.put(i, base);
            base *= 2;
        }
    }

    /**
     * Compute a socket address' sizeOfFingerTable bit identifier
     *
     * @param addr: socket address
     * @return sizeOfFingerTable-bit identifier in long type
     */
    public static long hashSocketAddress(InetSocketAddress addr) {
        int i = addr.hashCode();
        return hashHashCode(i);
    }

    /**
     * Compute a string's sizeOfFingerTable bit identifier
     *
     * @param s: string
     * @return sizeOfFingerTable-bit identifier in long type
     */
    public static long hashString(String s) {
        int i = s.hashCode();
        return hashHashCode(i);
    }

    /**
     * Compute a sizeOfFingerTable bit integer's identifier
     *
     * @param i: integer
     * @return sizeOfFingerTable-bit identifier in long type
     */
    private static long hashHashCode(int i) {

        //sizeOfFingerTable bit regular hash code -> byte[4]
        byte[] hashbytes = new byte[4];
        hashbytes[0] = (byte) (i >> 24);
        hashbytes[1] = (byte) (i >> 16);
        hashbytes[2] = (byte) (i >> 8);
        hashbytes[3] = (byte) (i /*>> 0*/);

        // try to create SHA1 digest
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // successfully created SHA1 digest
        // try to convert byte[4]
        // -> SHA1 result byte[]
        // -> compressed result byte[4]
        // -> compressed result in long type
        if (md != null) {
            md.reset();
            md.update(hashbytes);
            byte[] result = md.digest();

            byte[] compressed = new byte[4];
            for (int j = 0; j < 4; j++) {
                byte temp = result[j];
                for (int k = 1; k < sizeOfFingerTable; k++) {
                    temp = (byte) (temp ^ result[j + k]);
                }
                compressed[j] = temp;
            }

            long ret = (compressed[0] & 0xFF) << 24 | (compressed[1] & 0xFF) << 16 | (compressed[2] & 0xFF) << 8 | (compressed[3] & 0xFF);
            ret = ret & (long) 0xFFFFFFFFl;
            return ret;
        }
        return 0;
    }


    public static long computeRelativeId(long universal, long local) {
        long ret = universal - local;
        if (ret < 0) {
            ret += powerOfTwo.get(sizeOfFingerTable);
        }
        return ret;
    }

    /**
     * Compute a socket address' SHA-1 hash in hex
     * and its approximate position in string
     *
     * @param addr
     * @return
     */
    public static String hexIdAndPosition(InetSocketAddress addr) {
        long hash = hashSocketAddress(addr);
        return (longTo8DigitHex(hash) + " (" + hash * 100 / Util.getPowerOfTwo(sizeOfFingerTable) + "%)");
    }


    public static String longTo8DigitHex(long l) {
        String hex = Long.toHexString(l);
        int lack = 8 - hex.length();
        StringBuilder sb = new StringBuilder();
        for (int i = lack; i > 0; i--) {
            sb.append("0");
        }
        sb.append(hex);
        return sb.toString();
    }


    public static long ithStart(long nodeid, int i) {
        return (nodeid + powerOfTwo.get(i - 1)) % powerOfTwo.get(sizeOfFingerTable);
    }

    /**
     * Get power of 2
     *
     * @param k
     * @return 2^k
     */
    public static long getPowerOfTwo(int k) {
        return powerOfTwo.get(k);
    }

    /**
     * Generate requested address by sending request to server
     *
     * @param server
     * @param req:   request
     * @return generated socket address,
     * might be null if
     * (1) invalid input
     * (2) response is null (typically cannot send request)
     * (3) fail to create address from reponse
     */
    public static InetSocketAddress requestAddress(InetSocketAddress server, String req) {

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

    public static String sendRequest(InetSocketAddress server, String req) {

        // invalid input
        if (server == null || req == null)
            return null;

        Socket talkSocket = null;

        // try to open talkSocket, output request to this socket
        // return null if fail to do so
        try {
            talkSocket = new Socket(server.getAddress(), server.getPort());
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
            System.out.println("Cannot get input stream from " + server.toString() + "\nRequest is: " + req + "\n");
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

    /**
     * Create InetSocketAddress using ip address and port number
     *
     * @return created InetSocketAddress object;
     * return null if:
     * (1) not valid input
     * (2) cannot find split input into ip and port strings
     * (3) fail to parse ip address.
     */
    public static InetSocketAddress createSocketAddress(String address) {
        if (address == null) {
            return null;
        }

        // split input
        String[] parts = address.split(":");

        //split string contains ":"
        if (parts.length == 2) {

            //get and pre-process ip address string
            String ip = parts[0];

            if (ip.charAt(0) == '/') {
                ip = ip.substring(1);
            }

            InetAddress ipAddress = null;

            try {
                ipAddress = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                System.out.println("Cannot create ip address: " + ip);
                return null;
            }

            int port = Integer.parseInt(parts[1]);
            return new InetSocketAddress(ipAddress, port);
        }

        // split string does not contain ":"
        else {
            return null;
        }
    }

    /**
     * Read one line from input stream
     *
     * @param in: input steam
     * @return line, might be null if:
     * (1) invalid input
     * (2) cannot read from input stream
     */
    public static String inputStreamToString(InputStream in) {

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