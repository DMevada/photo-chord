package edu.scu;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author Raghav Bhandari
 * @author Krishna Kandhani
 * @author Abhiman Kolte
 * @author Dhruv Mevada
 */
public class Node
{
    private InetSocketAddress selfAddress;

    public Node(InetSocketAddress address)
    {
        selfAddress = address;
    }

    public InetSocketAddress getSelfAddress()
    {
        return selfAddress;
    }

}
