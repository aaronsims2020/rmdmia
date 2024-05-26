package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet;

/**
 * org.happy.artist.rmdmia.rcsm.client.transport.udpros.packet.UDPMultiPacketMessageFactory implements UDPMessageFactory. Used to generate multi packet UDP messages. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class UDPMultiPacketMessageFactory implements UDPMessageFactory
{

    @Override
    public UDPMessage newUDPMessage(int messageID) 
    {
        return new UDPMultiPacketMessage(messageID);
    }
    
}
