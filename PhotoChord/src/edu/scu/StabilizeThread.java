package edu.scu;

import java.net.InetSocketAddress;

/**
 * Stabilize thread that periodically asks successor for its predecessor
 * and determine if current node should update or delete its successor.
 *
 *  @author Raghav Bhandari
 *  @author Krishna Kandhani
 *  @author Abhiman Kolte
 *  @author Dhruv Mevada
 */

public class StabilizeThread extends Thread {

    private Node local;
    private boolean alive;

    public StabilizeThread(Node _local) {
        local = _local;
        alive = true;
    }

    @Override
    public void run() {
        while (alive) {
            InetSocketAddress successor = local.getSuccessor();
            if (successor == null || successor.equals(local.getSelfAddress())) {
                local.updateFingers(-3, null); //fill
            }
            successor = local.getSuccessor();
            if (successor != null && !successor.equals(local.getSelfAddress())) {

                // try to get my successor's predecessor
                InetSocketAddress x = Util.requestAddress(successor, "YOURPRE");

                // if bad connection with successor! delete successor
                if (x == null) {
                    local.updateFingers(-1, null);
                }

                // else if successor's predecessor is not itself
                else if (!x.equals(successor)) {
                    long local_id = Util.hashSocketAddress(local.getSelfAddress());
                    long successor_relative_id = Util.computeRelativeId(Util.hashSocketAddress(successor), local_id);
                    long x_relative_id = Util.computeRelativeId(Util.hashSocketAddress(x),local_id);
                    if (x_relative_id>0 && x_relative_id < successor_relative_id) {
                        local.updateFingers(1,x);
                    }
                }

                // successor's predecessor is successor itself, then notify successor
                else {
                    local.notify(successor);
                }
            }

            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void toDie() {
        alive = false;
    }
}