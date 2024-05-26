package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data;

/**
 * org.happy.artist.rmdmia.rcsm.client.transport.tcpros.data.TCPSinglePacketMessageFactory implements TCPMessageFactory. Used to generate single packet TCP message Objects. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class TCPSinglePacketMessageFactory implements TCPMessageFactory
{
    @Override
    /** Create a new single packet TCP message. */
    public TCPMessage newTCPMessage(int messageID) 
    {
        return new TCPPacketMessage(messageID);
    }
    
}
