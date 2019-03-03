package edu.scu;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Helper {

    public static InetSocketAddress createSocketAddress (String addr) {

        // input null, return null
        if (addr == null) {
            return null;
        }

        // split input into ip string and port string
        String[] splitted = addr.split(":");

        // can split string
        if (splitted.length >= 2) {

            //get and pre-process ip address string
            String ip = splitted[0];
            if (ip.startsWith("/")) {
                ip = ip.substring(1);
            }

            //parse ip address, if fail, return null
            InetAddress m_ip = null;
            try {
                m_ip = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                System.out.println("Cannot create ip address: "+ip);
                return null;
            }

            // parse port number
            String port = splitted[1];
            int m_port = Integer.parseInt(port);

            // combine ip addr and port in socket address
            return new InetSocketAddress(m_ip, m_port);
        }

        // cannot split string
        else {
            return null;
        }

    }

}
