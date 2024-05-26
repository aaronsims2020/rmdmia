package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data;

/** TCPMessage.java is a TCP message Object implementation for TCPROS Transport 
 *  protocol.  
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public interface TCPMessage 
{
     /** returns the block ordered TCPROS Packet array. */ 
    public TCPMessage.Packet[] getPackets();   

    /** Return the message once all packets arrive. Otherwise return null. */
    public byte[] getMessage();       
    
    /** Optional method implemented by RMDMIA Cache Pools. Called by the Object 
     *  Cache Pool at checkin(). */
    public void recycle();    
    
    /** Prepare the Object for Shutdown. */
    public void shutdown();
    
    public class Packet
    {
        public int blockID;
        public byte[] bytes;
        /** block id -1 means this object is not longer valid. Not setting byte[] due to Object instantiation time. */
        public void recycle()
        {
//            System.out.println("Calling recycle on block id: " + blockID);
            this.blockID=-1;
            this.bytes=null;
        }
    }
}
