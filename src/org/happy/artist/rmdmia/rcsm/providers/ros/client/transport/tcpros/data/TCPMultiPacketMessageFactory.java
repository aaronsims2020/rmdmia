package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data;

/**
 * org.happy.artist.rmdmia.rcsm.client.transport.tcpros.data.TCPMultiPacketMessageFactory implements TCPMessageFactory. Used to generate multi packet TCP messages. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class TCPMultiPacketMessageFactory implements TCPMessageFactory
{

    @Override
    public TCPMessage newTCPMessage(int messageID) 
    {
        return new TCPMultiPacketMessage(messageID);
    }
    
}
