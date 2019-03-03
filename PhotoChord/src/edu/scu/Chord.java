package edu.scu;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Chord {

    private InetAddress _contact;

    public static void main(String[] args) throws UnknownHostException {
        String localIP = InetAddress.getLocalHost().getHostAddress();

	// Process the args here
        if(args.length == 1) {
            // Create new ring

        }

        if(args.length == 3) {
            // Join existing ring
        }


    }
}