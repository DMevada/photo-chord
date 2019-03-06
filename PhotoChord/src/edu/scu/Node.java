package edu.scu;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * @author Raghav Bhandari
 * @author Krishna Kandhani
 * @author Abhiman Kolte
 * @author Dhruv Mevada
 */
public class Node
{
    private InetSocketAddress selfAddress;
    private InetSocketAddress pred;
    private HashMap<Integer, InetSocketAddress> fingerTable;

    public Node(InetSocketAddress address)
    {
        selfAddress = address;
    }

    public InetSocketAddress getSelfAddress()
    {
        return selfAddress;
    }

    public boolean join (InetSocketAddress contact)
    {

        // if contact is other node (join ring), try to contact that node
        // (contact will never be null)
        if (contact != null && !contact.equals(selfAddress))
        {
            InetSocketAddress successor = Util.requestAddress(contact, "FINDSUCC_" + localId);
            if (successor == null)  {
                System.out.println("\nCannot find node you are trying to contact. Please exit.\n");
                return false;
            }
            updateIthFinger(1, successor);
        }

//        // start all threads
//        listener.start();
//        stabilize.start();
//        fix_fingers.start();
//        ask_predecessor.start();

        return true;
    }

}
